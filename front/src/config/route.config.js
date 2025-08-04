export const APP_ROUTE = {
    // Auth
    SIGN_IN: 'sign-in',
    SIGN_UP: 'sign-up',
    FORGOT_PASSWORD: 'forgot-password',
    RESET_PASSWORD: 'reset-password',
    
    // Dashboard
    DASHBOARD: 'dashboard',
    INSTITUTIONS: 'institutions',
    DEPARTMENTS: 'departments',
    SCHEDULES: 'schedules',
    SHIFTS: 'shifts',
    ACCOUNTS: 'accounts',

    // Schedule
    GENERATE_SCHEDULE: 'generate-schedule'
}

/**
 * 
 * @param  {...String} args 
 * @returns 
 */
export const buildRoute = (...args) => {
    if (!args || args?.length == 0) {
        return ''
    }
    return args.join('/')
}