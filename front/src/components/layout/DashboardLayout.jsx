import React from "react";
import { NavLink, Outlet, useLocation } from "react-router";
import { APP_CONFIG } from "../../config/app.config";
import { APP_MENUS } from "../../config/menus.config";
import { formatText, trimSlashes } from "../../helpers/utils";
import LucideIcon from "../LucideIcon";
import { useDispatch } from "react-redux";
import { logout } from "../../helpers/redux/slices/authSlice";
import useCheckUser from "../../hooks/useCheckUser";
import { DOMAIN } from "../../config/permission.config";

const DashboardLayout = () => {
	const { pathname } = useLocation();
    const { isUserDomain, user } = useCheckUser()
    const dispatch = useDispatch()

    const handleLogout = () => {
        dispatch(logout())
    }

	return (
		// <div className="min-h-screen bg-gray-50">
        <>

        <div className="h-screen w-screen overflow-hidden">
        {isUserDomain(DOMAIN.INSTITUTION) && <p className="bg-blue-700 text-white font-bold p-1 text-center text-sm">{formatText(user?.institution)}</p>}
		<div className="flex-1 flex">
			{/* <div className="fixed inset-0 bg-black/50 z-40 lg:hidden"></div> */}
			{/* <div className="fixed left-0 top-0 z-50 h-screen bg-white border-r border-gray-200 transition-all duration-300 w-64"> */}
			<div className={`floating relative bg-white border-r border-gray-200 transition-all duration-300 w-64 ${isUserDomain(DOMAIN.INSTITUTION) ? "h-[calc(100vh-40px)]" : "h-screen"}`}>
				<div className="flex items-center justify-between p-4 border-b border-gray-200">
					<div className="w-full flex items-center space-x-2">
						<div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg flex items-center justify-center pointer">
							<svg
								xmlns="http://www.w3.org/2000/svg"
								width="24"
								height="24"
								viewBox="0 0 24 24"
								fill="none"
								stroke="currentColor"
								stroke-width="2"
								stroke-linecap="round"
								stroke-linejoin="round"
								className="lucide lucide-calendar w-5 h-5 text-white"
							>
								<path d="M8 2v4"></path>
								<path d="M16 2v4"></path>
								<rect
									width="18"
									height="18"
									x="3"
									y="4"
									rx="2"
								></rect>
								<path d="M3 10h18"></path>
							</svg>
						</div>
						<h1 className="flex-1 text-sm font-bold text-gray-900">
							{APP_CONFIG.TITLE}
						</h1>

						<div className="w-8 h-8 bg-slate-200 text-black rounded-xs flex items-center justify-center">
							<svg
								xmlns="http://www.w3.org/2000/svg"
								width="24"
								height="24"
								viewBox="0 0 24 24"
								fill="none"
								stroke="currentColor"
								stroke-width="2"
								stroke-linecap="round"
								stroke-linejoin="round"
								className="lucide lucide-menu w-4 h-4"
							>
								<line x1="4" x2="20" y1="12" y2="12"></line>
								<line x1="4" x2="20" y1="6" y2="6"></line>
								<line x1="4" x2="20" y1="18" y2="18"></line>
							</svg>
						</div>
					</div>
				</div>
				<nav className="mt-6 px-3">
					<ul className="space-y-1">
						{APP_MENUS.map((menu) => {
							return (
								<li>
									<NavLink
										aria-current="page"
										className={({ isActive }) =>
											isActive &&
											trimSlashes(menu?.path) ==
												trimSlashes(pathname)
												? "group active flex gap-2 items-center px-3 py-2 rounded-lg text-sm font-medium transition-colors border-r-2 bg-blue-50 text-blue-700 border-blue-700"
												: "group inactive flex gap-2 items-center px-3 py-2 rounded-lg text-sm font-medium transition-colors bg-black-50 text-black-500"
										}
										// className="group flex items-center px-3 py-2 rounded-lg text-sm font-medium transition-colors border-r-2 bg-slate-50 text-black-700 border-black-700"
										to={`/${menu?.path}`}
									>
										<LucideIcon
											name={menu?.icon}
											width="15"
											height="15"
											className="group-[.inactive]:text-gray-500 group-[.active]:text-blue-500"
										/>
										<span className="group-[.inactive]:text-gray-500 group-[.active]:text-blue-500">
											{menu?.name}
										</span>
									</NavLink>
								</li>
							);
						})}
					</ul>
				</nav>
				<div className="absolute bottom-2 left-3 right-3 pt-4 border-t border-gray-200">
					<div className="flex gap-4 items-between pb-6">
						<div className="w-8 h-8 bg-slate-200 text-black rounded-xs flex items-center justify-center pointer">
							<LucideIcon
								name="BellRing"
								className="text-blue-500"
								width="15"
								height="15"
							/>
						</div>
						<div className="w-8 h-8 bg-slate-200 text-black rounded-xs flex items-center justify-center pointer">
							<LucideIcon
								name="LogOut"
								className="text-red-500 cursor-pointer"
								width="15"
								height="15"
                                onClick={handleLogout}
							/>
						</div>
					</div>
					<div className="text-center text-gray-500 text-xs">
						Copyright 2025
					</div>
				</div>
			</div>
			{/* <button className="fixed top-4 left-4 z-40 lg:hidden p-2 bg-white rounded-lg shadow-lg border border-gray-200">
				<svg
					xmlns="http://www.w3.org/2000/svg"
					width="24"
					height="24"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-width="2"
					stroke-linecap="round"
					stroke-linejoin="round"
					className="lucide lucide-menu w-5 h-5"
				>
					<line x1="4" x2="20" y1="12" y2="12"></line>
					<line x1="4" x2="20" y1="6" y2="6"></line>
					<line x1="4" x2="20" y1="18" y2="18"></line>
				</svg>
			</button> */}
			{/* <main className="lg:ml-64 min-h-screen"> */}
			<main className="flex-1 bg-slate-50 h-screen overflow-y-auto">
				<Outlet />
			</main>
		</div>
        </div>
        </>
	);
};

export default DashboardLayout;
