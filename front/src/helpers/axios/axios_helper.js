import axios from "axios";

export const baseUrl = import.meta.env.VITE_BASE_URL;

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
				const token = localStorage.getItem("token");
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
			transformResponse,
			transformResponseError
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