import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.EMPLOYEE.name],
})

const employeesApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        getEmployees: builder.query({
            query: (data) => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.GET_EMPLOYEES,
                "data": data
            }),
            providesTags: [SERVICES.EMPLOYEE.name],
        }),
    }),
})

export const { useGetEmployeesQuery } = employeesApi


