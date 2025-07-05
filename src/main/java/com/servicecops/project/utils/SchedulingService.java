package com.servicecops.project.utils;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.dtos.WeekDay;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.repositories.SystemUserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulingService {
    private Model model;
    private final SystemUserRepository systemUserRepository;
    private final com.servicecops.project.utils.MailService mailService;
    private final QuotesService quotesService;


    public String scheduleDaysOff(Model model) {
        // Devs list
//        List<SchedulingService> devs = initializationService.getDevs();

//        Collections.shuffle(devs);
//        Collections.shuffle(devs);

        Set<SystemUserModel> employees = getEmployees();

        List<SystemUserModel> employeesList = employees.stream().toList();
        Map<String, List<SystemUserModel>> waList = assignDevelopers(employeesList);

        JSONObject randomQuote = resolveQuote();

        model.addAttribute("schedule", waList);
        model.addAttribute("quote", randomQuote.getString("quote"));
        model.addAttribute("author", randomQuote.getString("author"));
        return "schedule";
    }

    private JSONObject resolveQuote() {
        String quote = "Challenges are what make life interesting and overcoming them is what makes life meaningful";
        String author = "Joshua J. Marine";

        var randomQuote = quotesService.randomAncientQuote();

        if (!randomQuote.isEmpty()) {
            quote = randomQuote.getString("quote");
            author = randomQuote.getString("author");
        }

        JSONObject quoteObject = new JSONObject();
        quoteObject.put("quote", quote);
        quoteObject.put("author", author);

        return quoteObject;
    }

    private Map<String, List<SystemUserModel>> assignDevelopers(List<SystemUserModel> employees) {
        // Days of the week
        List<WeekDay> weekDays = new ArrayList<>(new HashSet<>(getWeekDays()));

        // in case the employees' list is empty
        if (employees.isEmpty()) {
            throw new IllegalStateException("Please provide list of staff.");
        }

        // Map to hold final assignments
        Map<WeekDay, List<SystemUserModel>> weekdayAssignments = weekDays.stream()
                .collect(Collectors.toMap(day -> day, day -> new ArrayList<>()));

        // Map to track developer counts
        Map<SystemUserModel, Integer> developerAssignments = employees.stream()
                .collect(Collectors.toMap(dev -> dev, dev -> 0, (existing, replacement) -> existing));

        // Assign each developer at least once
        enforceMinimumAssignments(employees, weekDays, weekdayAssignments, developerAssignments);

        // Assign remaining slots while enforcing constraints
        assignRemainingSlots(employees, weekDays, weekdayAssignments, developerAssignments);

        // Sort by weekday ID
        // Use weekday name as the key
        // Flatten and remove duplicates
        // Merge function (not needed but required by toMap)
        // Use LinkedHashMap to maintain insertion order
        return weekdayAssignments.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().id())) // Sort by weekday ID
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(), // Use weekday name as the key
                        entry -> entry.getValue().stream().distinct().toList(), // Flatten and remove duplicates
                        (existing, replacement) -> existing, // Merge function (not needed but required by toMap)
                        LinkedHashMap::new // Use LinkedHashMap to maintain insertion order
                ));
    }

    private Set<SystemUserModel> getEmployees() {
        Set<SystemUserModel> users = systemUserRepository.findAll().stream().collect(Collectors.toSet());
        // Minus any repetitions
        return users;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 7)
    public void makeSchedule() {
        Set<SystemUserModel> emps = getEmployees();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<SystemUserModel> employees = getEmployees().stream().toList();
        Map<String, List<SystemUserModel>> waList = assignDevelopers(employees);

        sendMailToDevs(emps, waList, executor);
    }

    private void enforceMinimumAssignments(
            List<SystemUserModel> developers,
            List<WeekDay> weekdays,
            Map<WeekDay, List<SystemUserModel>> weekdayAssignments,
            Map<SystemUserModel, Integer> developerAssignments) {
        Collections.shuffle(developers);

        int currentDayIndex = 0;

        log.info("[+] Enforcing minimum assignments...");
        for (SystemUserModel emp : developers) {
            WeekDay day = weekdays.get(currentDayIndex % weekdays.size());
            log.warn("[+] Adding {} to {}...", emp.getFirstName(), day.name());
            weekdayAssignments.get(day).add(emp);
            developerAssignments.put(emp, developerAssignments.get(emp) + 1);
            currentDayIndex++;
        }
    }

    private void assignRemainingSlots(
            List<SystemUserModel> developers,
            List<WeekDay> weekdays,
            Map<WeekDay, List<SystemUserModel>> weekdayAssignments,
            Map<SystemUserModel, Integer> developerAssignments) {
        Collections.shuffle(developers);

        final int MAX_DEVS_PER_DAY = 2;
//        final int MAX_DAYS_PER_DEV = 1;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (WeekDay day : weekdays) {
            log.info("****** {} ******", day.name());
//            int maxDevsForDay = (day.name().equalsIgnoreCase("Monday") || day.name().equalsIgnoreCase("Tuesday"))
//                    ? 1
//                    : MAX_DEVS_PER_DAY;
            int maxDevsForDay = MAX_DEVS_PER_DAY;
            List<SystemUserModel> assignedDevs = weekdayAssignments.get(day);

            log.info("[+] Max devs {}", maxDevsForDay);

            // Attempts to limit the number of attempts to execute canAssign to avoid infinite loop
            int attempts = 0;

            // Continue until the devs per day condition is satisfied
            while (assignedDevs.size() <= maxDevsForDay && attempts < developers.size() * 2) {
                SystemUserModel randomEmp = developers.get(random.nextInt(developers.size()));
                log.info("[+] Assigning {}\n\n", randomEmp.getFirstName());

                if (canAssign(randomEmp, day, weekdayAssignments, developerAssignments, weekdays, maxDevsForDay)) {
                    assignedDevs.add(randomEmp);
                    // Get or default handles unassigned devs gracefully
                    developerAssignments.put(randomEmp, developerAssignments.getOrDefault(randomEmp, 0) + 1);
                }
                attempts++;
            }
        }
    }

    private boolean canAssign(
            SystemUserModel emp,
            WeekDay currentDay,
            Map<WeekDay, List<SystemUserModel>> weekdayAssignments,
            Map<SystemUserModel, Integer> developerAssignments,
            List<WeekDay> weekdays,
            int maxDevsPerDay) {

        log.info("[+] Checking if {} is assigned", emp.getFirstName());
        // Check if a developer already reached their max-allowed days
        Integer devAssignments = developerAssignments.getOrDefault(emp, 0);
        if (devAssignments >= 2) {
            return false;
        }

        // Check if the current day already has max developers assigned
        log.info("[+] Checking if weekday {} has max developers...", currentDay.name());
        List<SystemUserModel> currentDayAssignments = weekdayAssignments.getOrDefault(currentDay, new ArrayList<>());
        if (currentDayAssignments.size() >= maxDevsPerDay) {
            log.info("[+] Yes it does ...");
            return false;
        }

        log.info("[+] Checking if {} has been assigned to the previous day...", emp.getFirstName());
        // Check if the developer is already assigned to the previous day
        int currentIndex = weekdays.indexOf(currentDay);
        if (currentIndex > 0) {
            WeekDay previousDay = weekdays.get(currentIndex - 1);
            List<SystemUserModel> previousDayAssignments = weekdayAssignments.getOrDefault(previousDay, new ArrayList<>());

            // Ensure the developer is not assigned to both previous and current day
            log.info("[+] Status {}", String.valueOf(!previousDayAssignments.contains(emp) && !currentDayAssignments.contains(emp)).toUpperCase());
            return !previousDayAssignments.contains(emp) && !currentDayAssignments.contains(emp);
        }

        // If all checks pass, assignment is allowed
        return true;
    }


    private void sendMailToDevs(Set<SystemUserModel> developers, Map<String, List<SystemUserModel>> weekdayAssignments, ExecutorService executor) {
//        CountDownLatch latch = new CountDownLatch(developers.size());
        try {
            developers.forEach(
                    dev -> executor.submit(
                            () -> {
                                try {
                                    mailService.dispatchNotificationMail(
                                            dev,
                                            "Weeks Schedule",
                                            weekdayAssignments,
                                            resolveQuote(),
                                            "email-template"
                                    );
                                } catch (MessagingException | UnsupportedEncodingException e) {
                                    throw new RuntimeException(e.getMessage(), e);
                                }
                            }
                    )
            );
        } finally {
            log.info("[+] Initiating shut down...");
            shutdownExecutor(executor);
        }
    }


    public List<WeekDay> getWeekDays() {
        List<WeekDay> weekDays = new ArrayList<>();
        WeekDay dayOne = new WeekDay(1, "Monday");
        weekDays.add(dayOne);

        WeekDay dayTwo = new WeekDay(2, "Tuesday");
        weekDays.add(dayTwo);

        WeekDay dayThree = new WeekDay(3, "Wednesday");
        weekDays.add(dayThree);

        WeekDay dayFour = new WeekDay(4, "Thursday");
        weekDays.add(dayFour);

        WeekDay dayFive = new WeekDay(5, "Friday");
        weekDays.add(dayFive);

        return weekDays;
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            // Wait for all tasks to complete or timeout after 1 minute
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
