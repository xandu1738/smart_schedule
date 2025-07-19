import React, { useState } from "react";
import { useParams, useNavigate } from "react-router";
import { ArrowLeft, Building2, Users, User, Calendar } from "lucide-react";
import AddButton from "../../components/AddButton";
import ShiftCard from "../../components/ShiftCard";
import Spinner from "../../components/Spinner";
import AddShift from "./AddShift";
import { useGetSingleDepartmentQuery } from "../../helpers/redux/slices/extendedApis/departmentsApi";

const DepartmentDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [showCreateShift, setShowCreateShift] = useState(false);

  const {
    data: department,
    isLoading,
    error,
  } = useGetSingleDepartmentQuery({ departmentId: id });

  // Mock shifts data - replace with actual API call when available
  const [shifts] = useState([
    {
      id: 1,
      name: "Morning",
      employeeCount: 12,
      department: department?.data?.name || "Loading...",
      status: "Active",
    },
    {
      id: 2,
      name: "Evening",
      employeeCount: 8,
      department: department?.data?.name || "Loading...",
      status: "Active",
    },
  ]);

  const handleBack = () => {
    navigate("/dashboard/departments");
  };

  if (isLoading) {
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

      <div className="flex items-center justify-between mb-6">
        <div className="flex flex-col justify-start items-start">
          <h2 className="text-2xl font-bold text-gray-900">Shifts</h2>
          <p className="text-gray-600">
            Manage department shifts and schedules
          </p>
        </div>
        <AddButton
          onClick={() => setShowCreateShift(true)}
          buttonName="Create Shift"
        />
      </div>

      {/* Shifts Grid */}
      {shifts.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {shifts.map((shift) => (
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
        <div className="bg-white shadow-md rounded-lg p-8 text-center">
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

      {/* Create Shift Dialog */}
      {showCreateShift && (
        <AddShift
          setShowDialog={setShowCreateShift}
          departmentId={id}
          setRefetch={() => {
            // Refresh shifts data when available
            console.log("Refreshing shifts data");
          }}
        />
      )}
    </div>
  );
};

export default DepartmentDetails;
