import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.SHIFT.name],
})

const shiftsApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        getAllShifts: builder.query({
            query: (search) => ({
                "SERVICE": SERVICES.SHIFT.name,
                "ACTION": SERVICES.SHIFT.ACTION.GET_ALL,
                "data": {},
                search:search
            }),
            providesTags: [SERVICES.SHIFT.name],
        }),
        createShift: builder.mutation({
            query: (data) => ({
                SERVICE: SERVICES.SHIFT.name,
                ACTION: SERVICES.SHIFT.ACTION.CREATE_SHIFT,
                data: data,
            }),
            invalidatesTags: [SERVICES.SHIFT.name],
        }),
        shiftDetails: builder.query({
            query: (data) => ({
                SERVICE: SERVICES.SHIFT.name,
                ACTION: SERVICES.SHIFT.ACTION.SHIFT_DETAILS,
                data: data,
            }),
            providesTags: [SERVICES.SHIFT.name],
        }),
    }),
})

export const { useGetAllShiftsQuery, useCreateShiftMutation, useShiftDetailsQuery } = shiftsApi

