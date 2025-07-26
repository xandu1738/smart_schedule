import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.SCHEDULE.name],
})

const scheduleApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        saveSchedule: builder.mutation({
            query: (data) => ({
                "SERVICE": SERVICES.SCHEDULE.name,
                "ACTION": SERVICES.SCHEDULE.ACTION.SAVE,
                "data": data,
            }),
            invalidatesTags: [SERVICES.SCHEDULE.name],
        }),

        getSingleSchedule: builder.query({
            query: (data) => ({
                "SERVICE": SERVICES.SCHEDULE.name,
                "ACTION": SERVICES.SCHEDULE.ACTION.GET_SINGLE,
                "data": data,
            }),
            providesTags: [SERVICES.SCHEDULE.name],
        }),
    }),
})

export const { useSaveScheduleMutation, useGetSingleScheduleQuery } = scheduleApi

