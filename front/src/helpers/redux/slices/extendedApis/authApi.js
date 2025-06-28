import { baseApiSlice } from '../baseApiSlice';

const enhancedApi = baseApiSlice.enhanceEndpoints({
  addTagTypes: ['Auth']
});

const authApi = enhancedApi.injectEndpoints({
    endpoints: (builder) => ({
        login: builder.mutation({
            query: (data) => {
                return {
                    "ACTION": "login",
                    "SERVICE": "Auth",
                    "data": {
                        ...data
                    }
                }
            },
        }),
    }),
});

export const { useLoginMutation } = authApi;