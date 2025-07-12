import { createApi } from "@reduxjs/toolkit/query/react";
import { AxiosPrivate } from "../../axios/axios_helper";

export const successCodes = [200, 0, 201];

export const axiosBaseQuery = async (data) => {
    try {
        const result = await AxiosPrivate({
            method: 'POST',
            data,
            params: {},
        });

        if (!successCodes.includes(result?.data?.returnCode)) {
            return {
                error: {
                    code: result?.data?.returnCode,
                    message: result?.data?.returnMessage ?? "Something went wrong",
                },
            };
        }

        return { data: {
            code: result?.data?.returnCode,
            message: result?.data?.returnMessage,
            data: result?.data?.returnObject
         } };
    } catch (err) {
        return {
            error: {
                code: err?.response?.data?.returnCode ?? 500,
                message: err?.response?.data?.returnMessage ?? "Something went wrong",
            },
        };
    }
};

export const baseApiSlice = createApi({
    reducerPath: "api",
    baseQuery: axiosBaseQuery,
    endpoints: () => ({})
})