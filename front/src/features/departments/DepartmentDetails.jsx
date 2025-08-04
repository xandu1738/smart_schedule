import React, { useState } from "react";
import { useParams, useNavigate } from "react-router";
import { ArrowLeft, Building2, Users, User, Calendar } from "lucide-react";
import AddButton from "../../components/AddButton";
import ShiftCard from "../../components/ShiftCard";
import EmployeeCard from "../../components/EmployeeCard";
import Spinner from "../../components/Spinner";
import AddShift from "./AddShift";
import AddEmployee from "./AddEmployee";
import EditEmployee from "./EditEmployee";
import { useGetSingleDepartmentQuery } from "../../helpers/redux/slices/extendedApis/departmentsApi";
import { useGetAllShiftsQuery } from "../../helpers/redux/slices/extendedApis/shiftApi";
import {
  useGetEmployeesByDepartmentQuery,
  useDeleteEmployeeMutation,
} from "../../helpers/redux/slices/extendedApis/employeesApi";

const DepartmentDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [showCreateShift, setShowCreateShift] = useState(false);
  const [showAddEmployee, setShowAddEmployee] = useState(false);
  const [showEditEmployee, setShowEditEmployee] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [activeTab, setActiveTab] = useState("shifts");

  const {
    data: employees,
    isLoading: employeesLoading,
    refetch: refetchEmployees,
  } = useGetEmployeesByDepartmentQuery(parseInt(id));

  const [deleteEmployee, { isLoading: deleteLoading }] =
    useDeleteEmployeeMutation();

  const {
    data: department,
    isLoading,
    error,
  } = useGetSingleDepartmentQuery({ departmentId: id });

  const {
    data: shifts,
    isLoading: shiftsLoading,
    error: shiftsError,
  } = useGetAllShiftsQuery({ department_id: parseInt(id) });

  const handleBack = () => {
    navigate("/dashboard/departments");
  };

  const handleEditEmployee = (employee) => {
    setSelectedEmployee(employee);
    setShowEditEmployee(true);
  };

  const handleDeleteEmployee = async (employee) => {
    if (window.confirm(`Are you sure you want to delete ${employee.name}?`)) {
      try {
        await deleteEmployee({ id: employee.id });
        refetchEmployees();
        console.log("Employee deleted successfully");
      } catch (error) {
        console.error("Error deleting employee:", error);
      }
    }
  };

  if (isLoading || shiftsLoading || employeesLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <Spinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-red-500">Error loading department details</div>
      </div>
    );
  }

  const departmentData = department?.data;
  const shiftsData = shifts?.data || [];
  const employeesData = employees?.data || [];

  return (
    <div className="m-8">
      <button
        onClick={handleBack}
        className="flex items-center gap-2 text-gray-600 hover:text-gray-800 mb-6 transition-colors"
      >
        <ArrowLeft size={20} />
        <span className="text-sm font-medium">Back to Departments</span>
      </button>

      <div className="w-full bg-white shadow-md rounded-lg p-6 mb-8">
        <div className="flex items-start gap-4">
          <div className="bg-blue-600 rounded-lg p-3">
            <Building2 className="text-white" size={32} />
          </div>
          <div className="flex flex-col justify-start items-start">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              {departmentData?.name}
            </h1>
            <p className="text-gray-600 text-lg mb-2">
              {departmentData?.description}
            </p>

            <div className="flex items-center gap-6 text-sm text-gray-500">
              <div className="flex items-center gap-2">
                <Users size={16} />
                <span>{departmentData?.noOfEmployees || 0} employees</span>
              </div>
              <div className="flex items-center gap-2">
                <User size={16} />
                <span>
                  Manager: {departmentData?.managerName || "Not assigned"}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <div className="flex border-b border-gray-200">
          <button
            onClick={() => setActiveTab("shifts")}
            className={`flex-1 px-6 py-4 text-sm font-medium text-center transition-colors ${
              activeTab === "shifts"
                ? "text-blue-600 border-b-2 border-blue-600 bg-blue-50"
                : "text-gray-500 hover:text-gray-700 hover:bg-gray-50"
            }`}
          >
            Shifts ({shiftsData.length})
          </button>
          <button
            onClick={() => setActiveTab("employees")}
            className={`flex-1 px-6 py-4 text-sm font-medium text-center transition-colors ${
              activeTab === "employees"
                ? "text-blue-600 border-b-2 border-blue-600 bg-blue-50"
                : "text-gray-500 hover:text-gray-700 hover:bg-gray-50"
            }`}
          >
            Employees ({employeesData.length})
          </button>
        </div>

        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-2xl font-bold text-gray-900">
                {activeTab === "shifts" ? "Shifts" : "Employees"}
              </h2>
              <p className="text-gray-600">
                {activeTab === "shifts"
                  ? "Manage department shifts and schedules"
                  : "Manage department employees and staff"}
              </p>
            </div>
            <AddButton
              onClick={() =>
                activeTab === "shifts"
                  ? setShowCreateShift(true)
                  : setShowAddEmployee(true)
              }
              buttonName={
                activeTab === "shifts" ? "Create Shift" : "Add Employee"
              }
            />
          </div>

          {activeTab === "shifts" && (
            <>
              {shiftsData.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {shiftsData.map((shift) => (
                    <ShiftCard
                      key={shift.id}
                      shift={shift.name}
                      employeeCount={shift.employeeCount}
                      department={shift.department}
                      status={shift.status}
                    />
                  ))}
                </div>
              ) : (
                <div className="text-center py-12">
                  <div className="text-gray-400 mb-4">
                    <Calendar size={48} className="mx-auto" />
                  </div>
                  <h3 className="text-lg font-semibold text-gray-600 mb-2">
                    No shifts created yet
                  </h3>
                  <p className="text-gray-500 mb-4">
                    Create your first shift to get started with scheduling
                  </p>
                  <AddButton
                    onClick={() => setShowCreateShift(true)}
                    buttonName="Create First Shift"
                  />
                </div>
              )}
            </>
          )}

          {activeTab === "employees" && (
            <>
              {employeesData.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {employeesData.map((employee) => (
                    <EmployeeCard
                      key={employee.id}
                      name={employee.name}
                      email={employee.email}
                      number={employee.number}
                      department={departmentData?.name || employee.department}
                      status={employee.status}
                      role={employee.role}
                      employee={employee}
                      onEdit={handleEditEmployee}
                      onDelete={handleDeleteEmployee}
                    />
                  ))}
                </div>
              ) : (
                <div className="text-center py-12">
                  <div className="text-gray-400 mb-4">
                    <Users size={48} className="mx-auto" />
                  </div>
                  <h3 className="text-lg font-semibold text-gray-600 mb-2">
                    No employees added yet
                  </h3>
                  <p className="text-gray-500 mb-4">
                    Add your first employee to get started with team management
                  </p>
                  <AddButton
                    onClick={() => setShowAddEmployee(true)}
                    buttonName="Add First Employee"
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {showCreateShift && (
        <AddShift
          setShowDialog={setShowCreateShift}
          departmentId={id}
          setRefetch={() => {
            console.log("Refreshing shifts data");
          }}
        />
      )}

      {showAddEmployee && (
        <AddEmployee
          setShowDialog={setShowAddEmployee}
          departmentId={id}
          departmentName={departmentData?.name}
          setRefetch={() => {
            refetchEmployees();
            console.log("Refreshing employees data");
          }}
        />
      )}

      {/* Edit Employee Dialog */}
      {showEditEmployee && selectedEmployee && (
        <EditEmployee
          setShowDialog={setShowEditEmployee}
          employee={selectedEmployee}
          departmentName={departmentData?.name}
          setRefetch={() => {
            refetchEmployees();
            console.log("Refreshing employees data");
            setSelectedEmployee(null);
          }}
        />
      )}
    </div>
  );
};

export default DepartmentDetails;
