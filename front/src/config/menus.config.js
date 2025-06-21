import { APP_PERMISSIONS } from "./permission.config";
import { APP_ROUTE, buildRoute } from "./route.config";

export const APP_MENUS = [
	// {
	// 	name: "Institutions",
	// 	path: APP_ROUTE.INSTITUTIONS, // Should be provided if no child menus
	// 	permissions: APP_PERMISSIONS.CAN_VIEW_INSTITUTIONS,
	// 	childMenus: [
	// 		{
	// 			name: "Institutions",
	// 			path: buildRoute(APP_ROUTE.INSTITUTIONS),
	//             permissions: APP_PERMISSIONS.CAN_VIEW_DASHBOARD
	// 		},
	// 	],
	// },

	{
		name: "Dashboard",
		path: APP_ROUTE.DASHBOARD,
	},
	{
		name: "Institutions",
		path: APP_ROUTE.INSTITUTIONS,
		permissions: APP_PERMISSIONS.CAN_VIEW_INSTITUTIONS,
		childMenus: [
			{
				name: "Institutions",
				path: buildRoute(APP_ROUTE.INSTITUTIONS),
			},
		],
	},
	{
		name: "Departments",
		path: APP_ROUTE.DEPARTMENTS,
	},
	{
		name: "Schedules",
		path: APP_ROUTE.DEPARTMENTS,
	},
	{
		name: "Shifts",
		path: APP_ROUTE.DEPARTMENTS,
	},
];
