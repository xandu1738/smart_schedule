export const APP_ROUTE = {
    DASHBOARD: 'dashboard',
    INSTITUTIONS: 'institutions',
    DEPARTMENTS: 'departments',
    SCHEDULES: 'schedules',
    SHIFTS: 'shifts'
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