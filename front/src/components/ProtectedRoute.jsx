import React from "react";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../helpers/redux/slices/authSlice";
import { Navigate } from "react-router";
import { APP_ROUTE } from "../config/route.config";

const ProtectedRoute = ({
    requiresAuth = true,
    domain = [],
    permissions = [],
    children,
}) => {
    const isAuthenticated = useSelector(selectIsAuthenticated)
    if (!isAuthenticated && requiresAuth) {
        // early return on not authenticated
        return <Navigate to={`/${APP_ROUTE.SIGN_IN}`} />
    }
    return children;
};

export default ProtectedRoute;