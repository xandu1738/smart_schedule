import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.INSTITUTION.name],
})

const institutionsApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        getAllInstitutions: builder.query({
            query: () => ({
                "SERVICE": SERVICES.INSTITUTION.name,
                "ACTION": SERVICES.INSTITUTION.ACTION.GET_ALL,
                "data": {}
            }),
            providesTags: [SERVICES.INSTITUTION.name],
        }),
    }),
})

export const { useGetAllInstitutionsQuery } = institutionsApi

