import axios from "axios";

export const baseUrl = import.meta.env.VITE_BASE_URL;

export const AxiosPublic = axios.create({
	baseUrl,
	headers: {
		"Content-Type": "application/json",
	},
});

export const AxiosPrivate = axios.create({
	baseUrl,
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
		const transformResponse = (response) => {
			//TODO: Transform resposnse
			return response;
		};

		const transformResponseError = (error) => {
			//TODO: Transform resposnse
			return Promise.reject(error);
		};

		AxiosPrivate.interceptors.request.use(
			(config) => {
				//FIXME: To handle the authentication
				const token = localStorage.getItem("token");
				if (token) {
					config.headers.Authorization = `Bearer ${token}`;
				}
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
				//TODO: Transform request
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
