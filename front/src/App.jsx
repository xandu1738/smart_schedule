import "./App.css";
import Dashboard from "./features/dashboard/Dashboard.jsx";
import Departments from "./features/departments/Departments.jsx";
import Institutions from "./features/institutions/Institutions.jsx";
import Schedules from "./features/schedules/Schedules.jsx";
import Shift from "./features/shifts/Shift.jsx";
import { AxiosConfiguration } from "./helpers/axios_helper";
import { store } from "./helpers/store.js";

import React from "react";
import { Route, Routes } from "react-router";

const App = () => {
	// intiialize services
	AxiosConfiguration.initialize(store);

	return (
		<Routes>
			<Route index element={<Dashboard />} />
			<Route path="dashboard" element={<Dashboard />} />
			<Route path="institutions" element={<Institutions />}>
				<Route index element={<h1>Institution</h1>} />
			</Route>
			<Route path="departments" element={<Departments />}>
				<Route index element={<h1>Department</h1>} />
			</Route>
			<Route path="schedules" element={<Schedules />}>
				<Route index element={<h1>Schedule</h1>} />
			</Route>
			<Route path="shifts" element={<Shift />}>
				<Route index element={<h1>Shift</h1>} />
			</Route>
		</Routes>
	);
};

export default App;
