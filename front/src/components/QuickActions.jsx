import React, { useState } from "react";
import {
  Building,
  Building2,
  Calendar,
  Clock,
  User,
  Users,
} from "lucide-react";
import { useNavigate } from "react-router";
import AddEmployee from "../features/departments/AddEmployee";
import AddDepartment from "../features/departments/AddDepartment";
import AddShift from "../features/departments/AddShift";

const QuickActions = () => {
  const navigate = useNavigate();
  const [showAddEmployee, setShowAddEmployee] = useState(false);
  const [showAddDepartment, setShowAddDepartment] = useState(false);
  const [showAddShift, setShowAddShift] = useState(false);

  const handleAddEmployee = () => {
    // Navigate to departments page since we need to select a department first
    navigate("/dashboard/departments");
  };

  const handleCreateDepartment = () => {
    setShowAddDepartment(true);
  };

  const handleDefineShift = () => {
    // Navigate to departments page since we need to select a department first
    navigate("/dashboard/departments");
  };

  const handleGenerateSchedule = () => {
    // Navigate to schedules page (when available)
    navigate("/dashboard/schedules");
  };

  return (
    <section>
      <div className="flex flex-col shadow-md hover:shadow-lg rounded-lg p-4 m-4 bg-white w-auto">
        <h1 className="flex justify-start font-semibold mb-2">Quick Actions</h1>
        <div className="grid grid-cols-2 gap-4 mt-4 w-full">
          <button
            onClick={handleAddEmployee}
            className="flex border-2 border-dashed border-gray-300 flex-col items-center justify-center p-4 rounded-lg hover:border-blue-300 hover:bg-blue-100 transition-colors cursor-pointer group w-full"
          >
            <Users className="transition-colors group-hover:text-blue-500 text-gray-400" />
            <p className="transition-colors group-hover:text-blue-500 text-sm mt-1 font-medium text-gray-600">
              Add Employee
            </p>
          </button>
          <button
            onClick={handleCreateDepartment}
            className="flex border-2 border-dashed border-gray-300 flex-col items-center justify-center p-4 rounded-lg hover:border-blue-300 hover:bg-blue-100 transition-colors cursor-pointer group w-full"
          >
            <Building2 className="transition-colors group-hover:text-blue-500 text-gray-400" />
            <p className="transition-colors group-hover:text-blue-500 text-sm mt-1 font-medium text-gray-600">
              Create Department
            </p>
          </button>
          <button
            onClick={handleDefineShift}
            className="flex border-2 border-dashed border-gray-300 flex-col items-center justify-center p-4 rounded-lg hover:border-blue-300 hover:bg-blue-100 transition-colors cursor-pointer group w-full"
          >
            <Clock className="transition-colors group-hover:text-blue-500 text-gray-400" />
            <p className="transition-colors group-hover:text-blue-500 text-sm mt-1 font-medium text-gray-600">
              Define Shift
            </p>
          </button>
          <button
            onClick={handleGenerateSchedule}
            className="flex border-2 border-dashed border-gray-300 flex-col items-center justify-center p-4 rounded-lg hover:border-blue-300 hover:bg-blue-100 transition-colors cursor-pointer group w-full"
          >
            <Calendar className="transition-colors group-hover:text-blue-500 text-gray-400" />
            <p className="transition-colors group-hover:text-blue-500 text-sm mt-1 font-medium text-gray-600">
              Generate Schedule
            </p>
          </button>
        </div>
      </div>

      {/* Add Department Dialog */}
      {showAddDepartment && (
        <AddDepartment
          setShowDialog={setShowAddDepartment}
          setRefetch={() => {
            // Refresh departments data when available
            console.log("Refreshing departments data from quick actions");
          }}
        />
      )}
    </section>
  );
};

export default QuickActions;
