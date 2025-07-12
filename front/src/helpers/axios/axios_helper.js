import axios from "axios";
import { logout } from "../redux/slices/authSlice";

export const baseUrl = import.meta.env.VITE_BASE_URL;
export const AUTHENTICATION_REQUIRED_CODES = [401, 403];

/**
 * 
 * @param {import("@reduxjs/toolkit").EnhancedStore} store 
 * @returns 
 */
const getToken = (store) => store.getState().auth.token;

export const AxiosPublic = axios.create({
	baseURL: baseUrl,
	headers: {
		"Content-Type": "application/json",
	},
});

export const AxiosPrivate = axios.create({
	baseURL: baseUrl,
	headers: {
		"Content-Type": "application/json",
	},
});

export const AxiosConfiguration = {

    /**
     * 
     * @param {import("@reduxjs/toolkit").EnhancedStore} store 
     */
	initialize: (store) => {
		AxiosPrivate.interceptors.request.use(
			(config) => {
				//FIXME: To handle the authentication
				const token = getToken(store);
				if (token) {
					config.headers.Authorization = `Bearer ${token}`;
				}
                logRequest(config)

				return config;
			},
			(error) => {
				return Promise.reject(error);
			}
		);

		AxiosPrivate.interceptors.response.use(
			(response) => {
                if (AUTHENTICATION_REQUIRED_CODES.includes(response?.data?.returnCode)) {
                    //TODO: handle logout
                    store.dispatch(logout())
                }
				return response;
			},
			(error) => {
				return Promise.reject(error);
			}
		);

		AxiosPublic.interceptors.request.use(
			(config) => {
               logRequest(config)
				return config;
			},
			(error) => {
				return Promise.reject(error);
			}
		);

		AxiosPublic.interceptors.response.use(
			transformResponse,
			transformResponseError
		);
	},
};

const transformResponse = (response) => {
    //TODO: Transform resposnse
    return response;
};

const transformResponseError = (error) => {
    //TODO: Transform resposnse
    return Promise.reject(error);
};

export const logRequest = (config) => {
    config.headers['X-Request-Id'] = crypto.randomUUID();
    console.log(`REQ: ${config?.headers?.['X-Request-Id']} --- SERVICE => ${config?.data?.SERVICE} --- ACTION => ${config?.data?.ACTION} --- DATA => ${JSON.stringify(config?.data?.data)}`);
}