package com.servicecops.project.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuotesService {
    private static final String RANDOM_SINGLE_QUOTE_ENDPOINT = "https://zenquotes.io/api/random";
    private static final String QUOTE_KEY = "quote";
    private static final String AUTHOR_KEY = "author";

    public List<?> getRandomQuote() {
        RestClient restClient = RestClient.builder()
                .baseUrl(RANDOM_SINGLE_QUOTE_ENDPOINT)
                .build();

        Object body = restClient.get()
                .uri(RANDOM_SINGLE_QUOTE_ENDPOINT)
                .retrieve().body(Object.class);

        List<?> list = new ArrayList<>();

        if (body != null && body.getClass().isArray()) {
             var ls  = List.of(body);

             list = ls.stream()
                     .flatMap(o -> JSON.parseArray(o.toString()).stream())
                     .toList();

        } else if (body instanceof Collection) {
            list = new ArrayList<>((Collection<?>) body);
        }

        System.out.println(body);
        return list;
    }

    public JSONObject randomAnimeQuote() {
        List<JSONObject> quotes = animeQuotes();
        Random random = getRandom();
        int randomIndex = random.nextInt(quotes.size());
        return quotes.get(randomIndex);
    }

    private Random getRandom() {
        return new Random();
    }

    public JSONObject randomAncientQuote() {
        List<JSONObject> quotes = getAncientQuotes();
        Random random = getRandom();
        int randomIndex = random.nextInt(quotes.size());
        return quotes.get(randomIndex);
    }

    public List<JSONObject> animeQuotes() {
        List<JSONObject> quotes = new ArrayList<>();

        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A lesson without pain is meaningless. That's because you can't gain something without sacrificing something else.").fluentPut(AUTHOR_KEY, "Edward Elric (Fullmetal Alchemist: Brotherhood)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Fear is not evil. It tells you what your weakness is. And once you know your weakness, you can become stronger.").fluentPut(AUTHOR_KEY, "Gildarts Clive (Fairy Tail)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A person grows up when he's able to overcome hardships.").fluentPut(AUTHOR_KEY, "Jiraiya (Naruto)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Hard work is worthless for those that don’t believe in themselves.").fluentPut(AUTHOR_KEY, "Naruto Uzumaki (Naruto)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A person becomes strong when they have someone they want to protect.").fluentPut(AUTHOR_KEY, "Haku (Naruto)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The world’s not perfect. But it’s there for us, doing the best it can... that’s what makes it so damn beautiful.").fluentPut(AUTHOR_KEY, "Roy Mustang (Fullmetal Alchemist: Brotherhood)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "It's not about going faster. It's about never stopping.").fluentPut(AUTHOR_KEY, "Saitama (One Punch Man)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Power comes in response to a need, not a desire. You have to create that need.").fluentPut(AUTHOR_KEY, "Goku (Dragon Ball Z)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The only thing we're allowed to do is to believe that we won't regret the choice we made.").fluentPut(AUTHOR_KEY, "Levi Ackerman (Attack on Titan)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A flower does not think of competing with the flower next to it. It just blooms.").fluentPut(AUTHOR_KEY, "Zenitsu Agatsuma (Demon Slayer)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "No matter how deep the night, it always turns to day, eventually.").fluentPut(AUTHOR_KEY, "Brook (One Piece)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "You should enjoy the little detours. Because that's where you'll find the things more important than what you want.").fluentPut(AUTHOR_KEY, "Ging Freecss (Hunter x Hunter)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Forgetting is like a wound. The wound may heal, but it has already left a scar.").fluentPut(AUTHOR_KEY, "Monkey D. Luffy (One Piece)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "It’s not the face that makes someone a monster; it’s the choices they make with their lives.").fluentPut(AUTHOR_KEY, "Naruto Uzumaki (Naruto)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A sword is a weapon. The art of swordsmanship is learning how to protect.").fluentPut(AUTHOR_KEY, "Kenshin Himura (Rurouni Kenshin)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Sometimes, we have to look beyond what we want and do what's best.").fluentPut(AUTHOR_KEY, "Piccolo (Dragon Ball Z)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "No one knows what the future holds. That's why its potential is infinite.").fluentPut(AUTHOR_KEY, "Rintarou Okabe (Steins;Gate)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "It's not about winning or losing. It's about making the effort.").fluentPut(AUTHOR_KEY, "Seijuro Akashi (Kuroko's Basketball)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Even if things are painful and tough, people should appreciate what it means to be alive.").fluentPut(AUTHOR_KEY, "Yato (Noragami)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "There’s no shame in falling down! True shame is to not stand up again!").fluentPut(AUTHOR_KEY, "Shintarō Midorima (Kuroko's Basketball)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "You don’t die for your friends, you live for them!").fluentPut(AUTHOR_KEY, "Erza Scarlet (Fairy Tail)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The world is not beautiful, therefore it is.").fluentPut(AUTHOR_KEY, "Kino (Kino’s Journey)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Life is not a game of luck. If you wanna win, work hard.").fluentPut(AUTHOR_KEY, "Sora (No Game No Life)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A strong heart can overcome any obstacle.").fluentPut(AUTHOR_KEY, "Tsubaki Nakatsukasa (Soul Eater)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Even if I can't do it now, I'll get stronger and stronger until I can.").fluentPut(AUTHOR_KEY, "Asta (Black Clover)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Whatever you lose, you'll find it again. But what you throw away you'll never get back.").fluentPut(AUTHOR_KEY, "Kenshin Himura (Rurouni Kenshin)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "No matter how unlikely it seems, a path to the future will always open up.").fluentPut(AUTHOR_KEY, "Lelouch Lamperouge (Code Geass)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Our lives aren't just measured in years. They're measured by what we do with the time we have.").fluentPut(AUTHOR_KEY, "Makarov Dreyar (Fairy Tail)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "If you don’t take risks, you can’t create a future.").fluentPut(AUTHOR_KEY, "Monkey D. Luffy (One Piece)"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A person grows when he dares to face challenges.").fluentPut(AUTHOR_KEY, "Kenshin Himura (Rurouni Kenshin)"));

        return quotes;
    }

    public static List<JSONObject> getAncientQuotes() {
        List<JSONObject> quotes = new ArrayList<>();

        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The only true wisdom is in knowing you know nothing.").fluentPut(AUTHOR_KEY, "Socrates"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Knowing yourself is the beginning of all wisdom.").fluentPut(AUTHOR_KEY, "Aristotle"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "It does not matter how slowly you go as long as you do not stop.").fluentPut(AUTHOR_KEY, "Confucius"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Success is dependent on effort.").fluentPut(AUTHOR_KEY, "Sophocles"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "In the midst of chaos, there is also opportunity.").fluentPut(AUTHOR_KEY, "Sun Tzu"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The happiness of your life depends upon the quality of your thoughts.").fluentPut(AUTHOR_KEY, "Marcus Aurelius"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Patience is bitter, but its fruit is sweet.").fluentPut(AUTHOR_KEY, "Jean-Jacques Rousseau"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "It is not what we do, but also what we do not do, for which we are accountable.").fluentPut(AUTHOR_KEY, "Molière"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "We are what we repeatedly do. Excellence, then, is not an act, but a habit.").fluentPut(AUTHOR_KEY, "Aristotle"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Wisdom begins in wonder.").fluentPut(AUTHOR_KEY, "Socrates"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "He who is not a good servant will not be a good master.").fluentPut(AUTHOR_KEY, "Plato"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The superior man is modest in his speech but exceeds in his actions.").fluentPut(AUTHOR_KEY, "Confucius"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "If you are irritated by every rub, how will your mirror be polished?").fluentPut(AUTHOR_KEY, "Rumi"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Luck is what happens when preparation meets opportunity.").fluentPut(AUTHOR_KEY, "Seneca"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The more you know, the more you realize you don't know.").fluentPut(AUTHOR_KEY, "Aristotle"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A leader is best when people barely know he exists.").fluentPut(AUTHOR_KEY, "Lao Tzu"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "He who opens a school door, closes a prison.").fluentPut(AUTHOR_KEY, "Victor Hugo"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Time is a created thing. To say 'I don't have time' is like saying 'I don't want to.'").fluentPut(AUTHOR_KEY, "Lao Tzu"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Judge a man by his questions rather than his answers.").fluentPut(AUTHOR_KEY, "Voltaire"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "A journey of a thousand miles begins with a single step.").fluentPut(AUTHOR_KEY, "Lao Tzu"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Quality is not an act, it is a habit.").fluentPut(AUTHOR_KEY, "Aristotle"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The greater the obstacle, the more glory in overcoming it.").fluentPut(AUTHOR_KEY, "Molière"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "When anger rises, think of the consequences.").fluentPut(AUTHOR_KEY, "Confucius"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Waste no more time arguing what a good man should be. Be one.").fluentPut(AUTHOR_KEY, "Marcus Aurelius"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Education is the kindling of a flame, not the filling of a vessel.").fluentPut(AUTHOR_KEY, "Socrates"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Courage is knowing what not to fear.").fluentPut(AUTHOR_KEY, "Plato"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "The best revenge is to be unlike him who performed the injury.").fluentPut(AUTHOR_KEY, "Marcus Aurelius"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "Knowing others is intelligence; knowing yourself is true wisdom.").fluentPut(AUTHOR_KEY, "Lao Tzu"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "An investment in knowledge pays the best interest.").fluentPut(AUTHOR_KEY, "Benjamin Franklin"));
        quotes.add(new JSONObject().fluentPut(QUOTE_KEY, "He who conquers himself is the mightiest warrior.").fluentPut(AUTHOR_KEY, "Confucius"));

        return quotes;
    }
}
