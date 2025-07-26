import React from "react";
import AddButton from "../../components/AddButton";
import DepartmentCard from "../../components/DepartmentCard";
import AddDepartment from "./AddDepartment";
import EditDepartment from "./EditDepartment";
import { useGetAllDepartmentsQuery } from "../../helpers/redux/slices/extendedApis/departmentsApi";
import Spinner from "../../components/Spinner";
import { selectDomain } from "../../helpers/redux/slices/authSlice";
import { useSelector } from "react-redux";

const Departments = () => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [refetch, setRefetch] = React.useState(0);

  const { data, isLoading } = useGetAllDepartmentsQuery({});

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <Spinner />
      </div>
    );
  }

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
          {data?.data?.map((department, i) => (
          {data?.data?.map((department, i) => (
            <DepartmentCard
              key={department.id}
              departmentId={department.id}
              departmentName={department.name}
              institutionId={department.institutionId}
              description={department.description}
              employeeCount={department.noOfEmployees}
              employeeCount={department.noOfEmployees}
              managerName={department.managerName}
              department={department}
              setRefetch={setRefetch}
            />
          ))}
        </section>
      </div>
      {showDialog && (
        <AddDepartment setShowDialog={setShowDialog} setRefetch={setRefetch} />
      )}
    </>
  );
};

export default Departments;
