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
		icon: "LayoutDashboard",
		name: "Dashboard",
		path: APP_ROUTE.DASHBOARD,
	},
	{
		icon: "Landmark",
		name: "Institutions",
		path: buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.INSTITUTIONS),
		permissions: APP_PERMISSIONS.CAN_VIEW_INSTITUTIONS,
		childMenus: [
			{
				name: "Institutions",
				path: buildRoute(APP_ROUTE.INSTITUTIONS),
			},
		],
	},
	{
		icon: "Building",
		name: "Departments",
		path: buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.DEPARTMENTS),
	},
	{
		icon: "CalendarCheck",
		name: "Schedules",
		path: buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.SCHEDULES),
	},
	{
		icon: "ArrowLeftRight",
		name: "Shifts",
		path: buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.SHIFTS),
	},
    {
        icon: "User",
        name: "Accounts",
        path: buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.ACCOUNTS),
    },
];
