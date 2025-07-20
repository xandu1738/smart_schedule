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
    SHIFT: {
        name: "Shift",
        ACTION: {
            GET_ALL: "shifts",
            CREATE_SHIFT: "createShift",
            SHIFT_DETAILS: "shiftDetails",
        }
    },
    EMPLOYEE: {
        name: "Employee",
        ACTION: {
            ADD_EMPLOYEE: "save",
            GET_ALL: "getEmployees",
            EDIT_EMPLOYEE: "edit",
            DELETE_EMPLOYEE: "delete",
            GET_SINGLE_EMPLOYEE: "getEmployee",
        }
    }
}