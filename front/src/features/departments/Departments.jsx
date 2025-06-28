import React from "react";
import { Outlet } from "react-router";
import AddButton from "../../components/AddButton";
import DepartmentCard from "../../components/DepartmentCard";

const Departments = () => {
return (
	<>
		<div className="m-8">
			<section className="flex flex-row items-center justify-between">
				<div className="flex flex-col items-start justify-start left-0 w-full">
					<h1 className="font-bold text-3xl">Departments</h1>
					<p className="text-sm text-gray-500 pt-2">
						Manage your organization's departments
					</p>
				</div>
				<div>
					<AddButton onClick={() => {}} buttonName={"Add Department"} />
				</div>
			</section>
			<section className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
				<DepartmentCard
					department={"IT Department"}
					onClick={() => {}}
					employeeCount={10}
					description={"Responsible for all IT-related tasks and support."}
					managerName={"John Doe"}
				/>
				<DepartmentCard
					department={"IT Department"}
					onClick={() => {}}
					employeeCount={10}
					description={"Responsible for all IT-related tasks and support."}
					managerName={"John Doe"}
				/>
				<DepartmentCard
					department={"IT Department"}
					onClick={() => {}}
					employeeCount={10}
					description={"Responsible for all IT-related tasks and support."}
					managerName={"John Doe"}
				/>
				<DepartmentCard
					department={"IT Department"}
					onClick={() => {}}
					employeeCount={10}
					description={"Responsible for all IT-related tasks and support."}
					managerName={"John Doe"}
				/>
				<DepartmentCard
					department={"IT Department"}
					onClick={() => {}}
					employeeCount={10}
					description={"Responsible for all IT-related tasks and support."}
					managerName={"John Doe"}
				/>
			</section>
		</div>
	</>
);
};

export default Departments;
