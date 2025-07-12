import React from "react";
import { Outlet } from "react-router";

const Institutions = () => {
	return (
		<>
			<div>Institutions</div>
			<Outlet />
		</>
	);
};

export default Institutions;
