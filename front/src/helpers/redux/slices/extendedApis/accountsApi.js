import { SERVICES } from "../../../../config/app.config"
import { baseApiSlice } from "../baseApiSlice"

const enhancedApi = baseApiSlice.enhanceEndpoints({
    addTagTypes: [SERVICES.AUTH.name],
})

const accountsApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        getAllAccounts: builder.query({
            query: () => ({
                "SERVICE": SERVICES.AUTH.name,
                "ACTION": SERVICES.AUTH.ACTION.USERS_LIST,
                "data": {}
            }),
            providesTags: [SERVICES.AUTH.name],
        }),
    }),
})

export const { useGetAllAccountsQuery } = accountsApi


