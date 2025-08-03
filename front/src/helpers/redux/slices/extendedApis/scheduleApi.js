import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.SCHEDULE.name],
})

// {
//     "SERVICE":"Schedule",
//     "ACTION":"getMySchedules",
//     "data":{
//         "institutionId":2, // required for Back_Office domain
//         "departmentId":1
//     }
// }

const scheduleApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        saveSchedule: builder.mutation({
            query: (data) => ({
                "SERVICE": SERVICES.SCHEDULE.name,
                "ACTION": SERVICES.SCHEDULE.ACTION.SAVE,
                "data": data,
            }),
            invalidatesTags: [{ type: SERVICES.SCHEDULE.name, id: "LIST" }],
        }),

        getDepartmentSchedules: builder.query({
            query: (data) => ({
                "SERVICE": SERVICES.SCHEDULE.name,
                "ACTION": SERVICES.SCHEDULE.ACTION.GET_DEPARTMENT_SCHEDULES,
                "data": data
            }),
            providesTags: [{ type: SERVICES.SCHEDULE.name, id: "LIST" }]
        }),

        getSingleSchedule: builder.query({
            query: (data) => ({
                "SERVICE": SERVICES.SCHEDULE.name,
                "ACTION": SERVICES.SCHEDULE.ACTION.GET_SINGLE,
                "data": data,
            }),
            providesTags: (_, __, arg) => [{ type: SERVICES.SCHEDULE.name, id: arg.id }],
        }),
    }),
})

export const { 
    useSaveScheduleMutation,
    useGetSingleScheduleQuery,
    useGetDepartmentSchedulesQuery
} = scheduleApi

