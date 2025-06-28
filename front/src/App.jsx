import "./App.css";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import DashboardLayout from "./components/layout/DashboardLayout.jsx";
import { APP_PERMISSIONS } from "./config/permission.config.js";
import { APP_ROUTE } from "./config/route.config.js";
import Dashboard from "./features/dashboard/Dashboard.jsx";
import Departments from "./features/departments/Departments.jsx";
import Institutions from "./features/institutions/Institutions.jsx";
import Schedules from "./features/schedules/Schedules.jsx";
import Shift from "./features/shifts/Shift.jsx";
import { store } from "./helpers/redux/store.js";

import React from "react";
import { Route, Routes, Navigate } from "react-router";
import { AxiosConfiguration } from "./helpers/axios/axios_helper.js";

const App = () => {
	// intiialize services
	AxiosConfiguration.initialize(store);

	return (
		<Routes>
			<Route index element={<Navigate to={APP_ROUTE.DASHBOARD} />} />
			<Route path={APP_ROUTE.DASHBOARD} element={<DashboardLayout />}>
				<Route index element={<Dashboard />} />
				<Route
					path={APP_ROUTE.INSTITUTIONS}
					element={
						<ProtectedRoute
							permissions={APP_PERMISSIONS.CAN_VIEW_INSTITUTIONS}
						>
							<Institutions />
						</ProtectedRoute>
					}
				>
					<Route index element={<h1>Institution</h1>} />
				</Route>
				<Route path={APP_ROUTE.DEPARTMENTS} element={<Departments />}>
					<Route index element={<h1>Department</h1>} />
				</Route>
				<Route path={APP_ROUTE.SCHEDULES} element={<Schedules />}>
					<Route index element={<h1>Schedule</h1>} />
				</Route>
				<Route path={APP_ROUTE.SHIFTS} element={<Shift />}>
					<Route index element={<h1>Shift</h1>} />
				</Route>
			</Route>
		</Routes>
	);
};

export default App;
