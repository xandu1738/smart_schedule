import { useNavigate } from "react-router";

import { useState } from "react";
import Button from "../../components/Button";
import { APP_ROUTE, buildRoute } from "../../config/route.config";
import { useGetAllDepartmentsQuery } from "../../helpers/redux/slices/extendedApis/departmentsApi";
import { useSaveScheduleMutation } from "../../helpers/redux/slices/extendedApis/scheduleApi";
import Spinner from "../../components/Spinner";

const CreateSchedule = () => {
    const navigate = useNavigate()
    const [scheduleData, setScheduleData] = useState({
        name: "",
        departmentId: null,
        startTime: null,
        endTime: null,
        description: "",
    })

    const { data: departments, isLoading } = useGetAllDepartmentsQuery({});
    const [saveSchedule, { isLoading: isCreating }] = useSaveScheduleMutation()

    const handleNext = async () => {
        try {
            const response = await saveSchedule(scheduleData)
            if (response?.error) {
                throw new Error(response?.error?.message)
            }
            navigate(`/${buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.SCHEDULES, APP_ROUTE.GENERATE_SCHEDULE, "assign", response?.data?.data?.id)}`)
        } catch (error) {
            console.log(error)
        }
    }

    if (isLoading || isCreating) {
        return (<div className="flex items-center justify-center h-screen">
            <Spinner />
        </div>)
    }

    return (
        <div className="m-8 max-w-3xl mx-auto space-y-8">
            <section className="flex flex-row items-center justify-between">
                <div className="flex flex-col items-start justify-start left-0 w-full">
                    <h1 className="font-bold text-3xl">Create Schedule</h1>
                    <p className="text-sm text-gray-500 pt-2">
                        Define the basic information for your schedule
                    </p>
                </div>
            </section>
            <section className="rounded-lg border bg-white border-gray-200 shadow-sm p-8">
                <div className="p-0 pt-0 flex flex-col space-y-8">
                    <div className="flex flex-col space-y-2">
                        <label className="text-left text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="name">Schedule Name</label>
                        <input
                            id="name"
                            // className="border border-gray-200 rounded-sm p-2 outline-none focus:outline-none focus:border-gray-200 placeholder:text-gray-400 placeholder:font-medium" type="text" placeholder="Enter schedule name (e.g., IT Department - Week 1)" value={scheduleData.name} 
                            onChange={(e) => setScheduleData({ ...scheduleData, name: e.target.value })}
                            placeholder="Enter schedule name (e.g., IT Department - Week 1)"
                            required
                        />
                    </div>
                    <div className="flex flex-col space-y-2">
                        <label className="text-left text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="department">Department</label>
                        <select
                            id="department"
                            placeholder="Select department"
                            value={scheduleData.departmentId}
                            onChange={(e) => setScheduleData({ ...scheduleData, departmentId: e.target.value })}
                            required
                        >
                            <option value="">Select department</option>
                            {departments?.data?.map((dept) => (
                                <option key={dept.id} value={dept.id}>{dept.name}</option>
                            ))}
                        </select>
                    </div>
                    <div className="flex gap-4">
                        <div className="flex-1 flex flex-col space-y-1.5">
                            <label className="text-left text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="startTime">Start Time</label>
                            <input
                                id="startTime"
                                type="date"
                                value={scheduleData.startTime}
                                onChange={(e) => setScheduleData({ ...scheduleData, startTime: e.target.value })}
                                required
                            />

                        </div>
                        <div className="flex-1 flex flex-col space-y-1.5">
                            <label className="text-left text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="endTime">End Time</label>
                            <input
                                id="endTime"
                                type="date"
                                value={scheduleData.endTime}
                                onChange={(e) => setScheduleData({ ...scheduleData, endTime: e.target.value })}
                                required
                            />
                        </div>
                    </div>
                </div>

                <div className="flex justify-end space-x-3 pt-4">
                    <Button
                        onClick={handleNext}
                        disabled={isLoading || isCreating}
                        className="rounded-sm"
                        buttonName="Next"
                        type="button"
                    />
                </div>
            </section>
        </div>
    )
}

export default CreateSchedule



//     <div>
//   <div className="max-w-2xl mx-auto space-y-8">
//     {/* Header */}
//     <div>
//       <h1 className="text-3xl font-bold text-gray-900">Create New Schedule</h1>
//       <p className="text-gray-600 mt-2">Set up the basic information for your schedule</p>
//     </div>

//     {/* Form */}
//     <div className="rounded-lg border bg-card text-card-foreground shadow-sm">
//       <div className="flex flex-col space-y-1.5 p-6">
//         <h2 className="text-2xl font-bold leading-none tracking-tight">Schedule Information</h2>
//       </div>
//       <div className="p-6 pt-0">
//         <div>
//           <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="name">Schedule Name</label>
//           <InputText
//             id="name"
//             value={scheduleData.name}
//             onChange={(e) => setScheduleData({ ...scheduleData, name: e.target.value })}
//             placeholder="Enter schedule name (e.g., IT Department - Week 1)"
//           />
//         </div>

//         <div>
//           <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="department">Department</label>
//           {/* <Select onValueChange={(value) => setScheduleData({ ...scheduleData, department: value })}>
//             <SelectTrigger>
//               <SelectValue placeholder="Select department" />
//             </SelectTrigger>
//             <SelectContent>
//               {departments.map((dept) => (
//                 <SelectItem key={dept} value={dept}>{dept}</SelectItem>
//               ))}
//             </SelectContent>
//           </Select> */}
//           <select
//             id="department"
//             value={scheduleData.department}
//             onChange={(e) => setScheduleData({ ...scheduleData, department: e.target.value })}
//             className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-base ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
//           >
//             <option value="">Select department</option>
//             {departments.map((dept) => (
//               <option key={dept} value={dept}>{dept}</option>
//             ))}
//           </select>
//         </div>

//         <div className="grid grid-cols-2 gap-4">
//           <div>
//             <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="startDate">Start Date</label>
//             {/* <Popover>
//               <PopoverTrigger asChild>
//                 <Button
//                   variant="outline"
//                   className={cn(
//                     "w-full justify-start text-left font-normal",
//                     !scheduleData.startDate && "text-muted-foreground"
//                   )}
//                 >
//                   <CalendarIcon className="mr-2 h-4 w-4" />
//                   {scheduleData.startDate ? format(scheduleData.startDate, "PPP") : "Pick start date"}
//                 </Button>
//               </PopoverTrigger>
//               <PopoverContent className="w-auto p-0" align="start">
//                 <Calendar
//                   mode="single"
//                   selected={scheduleData.startDate}
//                   onSelect={(date) => setScheduleData({ ...scheduleData, startDate: date })}
//                   initialFocus
//                   className="pointer-events-auto"
//                 />
//               </PopoverContent>
//             </Popover> */}
//           </div>

//           <div>
//             <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="endDate">End Date</label>
//             {/* <Popover>
//               <PopoverTrigger asChild>
//                 <Button
//                   variant="outline"
//                   className={cn(
//                     "w-full justify-start text-left font-normal",
//                     !scheduleData.endDate && "text-muted-foreground"
//                   )}
//                 >
//                   <CalendarIcon className="mr-2 h-4 w-4" />
//                   {scheduleData.endDate ? format(scheduleData.endDate, "PPP") : "Pick end date"}
//                 </Button>
//               </PopoverTrigger>
//               <PopoverContent className="w-auto p-0" align="start">
//                 <Calendar
//                   mode="single"
//                   selected={scheduleData.endDate}
//                   onSelect={(date) => setScheduleData({ ...scheduleData, endDate: date })}
//                   initialFocus
//                   className="pointer-events-auto"
//                   disabled={(date) => scheduleData.startDate ? date < scheduleData.startDate : undefined}
//                 />
//               </PopoverContent>
//             </Popover> */}
//           </div>
//         </div>

//         <div>
//           <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70" htmlFor="description">Description (Optional)</label>
//           <InputText
//             id="description"
//             value={scheduleData.description}
//             onChange={(e) => setScheduleData({ ...scheduleData, description: e.target.value })}
//             placeholder="Brief description of the schedule"
//           />
//         </div>

//         <div className="flex justify-end space-x-3 pt-4">
//           {/* <Button variant="outline" onClick={() => navigate("/schedules")}>
//             Cancel
//           </Button> */}
//           <AddButton
//             onClick={() => {}}
//             disabled={true}
//             className="bg-blue-600 hover:bg-blue-700"
//           >
//             Next: Assign Employees
//             <ArrowRight className="w-4 h-4 ml-2" />
//           </AddButton>
//         </div>
//       </div>
//     </div>
//   </div>
// </div>
