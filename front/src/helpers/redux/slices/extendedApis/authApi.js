import { APP_CONFIG } from "../../../../config/app.config"
import { AxiosPublic } from "../../../axios/axios_helper"
import { successCodes } from "../baseApiSlice"

export const authApi = {
    login: (data) => {
        return new Promise((resolve, reject) => {
            AxiosPublic({
                method: "POST",
                data: {
                    "ACTION": "login",
                    "SERVICE": "Auth",
                    "data": {
                        ...data
                    }
                }
            }).then((res) => {
                if (successCodes.includes(res?.data?.returnCode)) {
                    resolve(res)
                }
                throw new Error(res?.data?.returnMessage || `Encountered an error while logging in. Contact support at ${APP_CONFIG.SUPPORT_EMAIL ?? "support@smart-skedue.com"}.`)
            }).catch((err) => {
                reject(err)
            })
        })
    }
}