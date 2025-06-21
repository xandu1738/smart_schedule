import React from "react";
import { Outlet } from "react-router";

const Departments = () => {
	return (
		<>
			<div>Departments</div>
			<Outlet />
		</>
	);
};

export default Departments;
