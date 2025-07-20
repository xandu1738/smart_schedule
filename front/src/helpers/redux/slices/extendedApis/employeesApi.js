import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.INSTITUTION.name],
})

const institutionsApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        getAllEmployees: builder.query({
            query: () => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.GET_ALL,
                "data": {}
            }),
            providesTags: [SERVICES.EMPLOYEE.name],
        }),
        addEmployee: builder.mutation({
            query: (data) => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.ADD_EMPLOYEE,
                "data": data
            }),
            invalidatesTags: [SERVICES.EMPLOYEE.name],
        }),
        editEmployee: builder.mutation({
            query: (data) => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.EDIT_EMPLOYEE,
                "data": data
            }),
            invalidatesTags: [SERVICES.EMPLOYEE.name],
        }),
        deleteEmployee: builder.mutation({
            query: (data) => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.DELETE_EMPLOYEE,
                "data": data
            }),
            invalidatesTags: [SERVICES.EMPLOYEE.name],
        }),
        getSingleEmployee: builder.query({
            query: (data) => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.GET_SINGLE_EMPLOYEE,
                "data": data
            }),
            providesTags: [SERVICES.EMPLOYEE.name],
        }),
        getEmployeesByDepartment: builder.query({
            query: (departmentId) => ({
                "SERVICE": SERVICES.EMPLOYEE.name,
                "ACTION": SERVICES.EMPLOYEE.ACTION.GET_ALL,
                "data": { department: departmentId }
            }),
            providesTags: [SERVICES.EMPLOYEE.name],
        }),
    }),
})

export const { useGetAllEmployeesQuery, useAddEmployeeMutation, useEditEmployeeMutation, useDeleteEmployeeMutation, useGetSingleEmployeeQuery, useGetEmployeesByDepartmentQuery } = institutionsApi

