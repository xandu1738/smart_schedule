export const APP_CONFIG = {
	TITLE: "Smart Skedue",
    SUPPORT_EMAIL: "support@smart-skedue.com",
};

export const SERVICES = {
    AUTH: {
        name: "Auth",
        ACTION: {
            LOGIN: "login",
            USERS_LIST: "usersList"
        }
    },
    INSTITUTION: {
        name: "Institution",
        ACTION: {
            GET_ALL: "getAll"
        }
    },
    DEPARTMENT: {
        name: "Department",
        ACTION: {
            GET_ALL: "getAll",
            SAVE: "save",
            DELETE: "delete",
            EDIT: "edit",
            GET_SINGLE: "getSingle"
        }
    },
}