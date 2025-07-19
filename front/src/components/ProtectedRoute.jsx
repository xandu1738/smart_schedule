import React from "react";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../helpers/redux/slices/authSlice";
import { Navigate, useLocation } from "react-router";
import { APP_ROUTE } from "../config/route.config";

const ProtectedRoute = ({
	requiresAuth = true,
	domain = [],
	permissions = [],
	children,
}) => {

    const location = useLocation()
    const isAuthenticated = useSelector(selectIsAuthenticated)

    if (!isAuthenticated && requiresAuth) {
        return <Navigate to={`/${APP_ROUTE.SIGN_IN}`} state={{ from: location?.pathname }} />
    }
	return children;
};

export default ProtectedRoute;
