import React from "react";
import { Outlet } from "react-router";

const Schedules = () => {
	return (
		<>
			<div>Schedules</div>
			<Outlet />
		</>
	);
};

export default Schedules;
