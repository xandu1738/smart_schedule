import { useSelector } from "react-redux"
import { selectUser } from "../helpers/redux/slices/authSlice"

const useCheckUser = () => {
    const user = useSelector(selectUser)

    /**
     * Check if user domain is the same as the provided domain
     * @param {string} domain 
     * @returns {boolean}
     */
    const isUserDomain = (domain) => {
        if (!user || !domain) return false

        return domain == user?.domain
    }

    /**
     * Check if user has permission
     * @param {string} permission 
     * @returns {boolean}
     */
    const isUserPermission = (permission) => {
        if (!user || !permission) return false

        return user?.permissions.includes(permission)
    }

    return {
        user,
        isUserDomain,
        isUserPermission
    }
}

export default useCheckUser