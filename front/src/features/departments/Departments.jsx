import React from "react";
import { Outlet } from "react-router";
import AddButton from "../../components/AddButton";
import DepartmentCard from "../../components/DepartmentCard";
import AddDepartment from "./AddDepartment";
import EditDepartment from "./EditDepartment";

const Departments = () => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [editMode, setEditMode] = React.useState(false);
  const [refetch, setRefetch] = React.useState(0);
  const [departments, setDepartments] = React.useState([]);

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
            <AddButton
              onClick={() => setShowDialog(true)}
              buttonName={"Add Department"}
            />
          </div>
        </section>
        <section className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {departments.map((department) => (
            <DepartmentCard
              key={department.id}
              department={department.name}
              onClick={() => setEditMode(true)}
              description={department.description}
              employeeCount={department.employeeCount}
              managerName={department.managerName}
            />
          ))}
        </section>
      </div>
      {showDialog && (
        <AddDepartment setShowDialog={setShowDialog} setRefetch={setRefetch} />
      )}
	  {editMode && (
		<EditDepartment setShowDialog={setEditMode} setRefetch={setRefetch} />
	  )}
    </>
  );
};

export default Departments;
