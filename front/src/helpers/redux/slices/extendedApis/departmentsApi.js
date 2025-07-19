import { SERVICES } from "../../../../config/app.config";
import { baseApiSlice } from "../baseApiSlice";

const enhancedApi = baseApiSlice.enhanceEndpoints({
  addTagTypes: [SERVICES.DEPARTMENT.name],
});

const departmentsApi = enhancedApi.injectEndpoints({
  endpoints: (builder) => ({
    getAllDepartments: builder.query({
      query: (data) => ({
        SERVICE: SERVICES.DEPARTMENT.name,
        ACTION: SERVICES.DEPARTMENT.ACTION.GET_ALL,
        data: data,
      }),
      providesTags: [SERVICES.DEPARTMENT.name],
    }),
    saveDepartment: builder.mutation({
      query: (data) => ({
        SERVICE: SERVICES.DEPARTMENT.name,
        ACTION: SERVICES.DEPARTMENT.ACTION.SAVE,
        data: data,
      }),
      invalidatesTags: [SERVICES.DEPARTMENT.name],
    }),
    deleteDepartment: builder.mutation({
      query: (data) => ({
        SERVICE: SERVICES.DEPARTMENT.name,
        ACTION: SERVICES.DEPARTMENT.ACTION.DELETE,
        data: data,
      }),
      invalidatesTags: [SERVICES.DEPARTMENT.name],
    }),
    editDepartment: builder.mutation({
      query: (data) => ({
        SERVICE: SERVICES.DEPARTMENT.name,
        ACTION: SERVICES.DEPARTMENT.ACTION.EDIT,
        data: data,
      }),
      invalidatesTags: [SERVICES.DEPARTMENT.name],
    }),
    getSingleDepartment: builder.query({
      query: (data) => ({
        SERVICE: SERVICES.DEPARTMENT.name,
        ACTION: SERVICES.DEPARTMENT.ACTION.GET_SINGLE,
        data: data,
      }),
      providesTags: [SERVICES.DEPARTMENT.name],
    }),
  }),
});

export const {
  useGetAllDepartmentsQuery,
  useSaveDepartmentMutation,
  useDeleteDepartmentMutation,
  useEditDepartmentMutation,
  useGetSingleDepartmentQuery,
} = departmentsApi;
