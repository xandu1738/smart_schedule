import React from "react";

const ProtectedRoute = ({
	requiresAuth = true,
	domain = [],
	permissions = [],
	children,
}) => {
	return children;
};

export default ProtectedRoute;
