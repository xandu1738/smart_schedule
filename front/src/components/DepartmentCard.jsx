import { Users, Pencil, Building2 } from "lucide-react";
import React from "react";
import { useNavigate } from "react-router";
import { APP_ROUTE } from "../config/route.config.js";
import EditDepartment from "../features/departments/EditDepartment.jsx";
import { useEditDepartmentMutation } from "../helpers/redux/slices/extendedApis/departmentsApi.js";

const DepartmentCard = ({
  department,
  employeeCount,
  description,
  managerName,
  departmentId,
  departmentName,
  setRefetch,
}) => {
  const [editMode, setEditMode] = React.useState(false);
  const [editDepartment] = useEditDepartmentMutation();
  const navigate = useNavigate();

  const handleCardClick = () => {
    const path = `/${APP_ROUTE.DASHBOARD}/${APP_ROUTE.DEPARTMENTS}/${departmentId}`;
    navigate(path);
  };

  return (
    <>
      <div
        className="shadow-md hover:shadow-lg rounded-lg p-4 m-4 bg-white w-auto cursor-pointer"
        onClick={() => handleCardClick()}
      >
        <div className="flex flex-row items-center justify-between mb-4">
          <div className="bg-blue-600 rounded-lg p-3 transition-colors">
            <Building2 className="text-white" />
          </div>
          <button
            onClick={(e) => {
              e.stopPropagation(); // Prevent card click
              setEditMode(true);
            }}
            className="text-sm"
          >
            <Pencil size={20} />
          </button>
        </div>
        <div className="flex flex-col items-start">
          <p className="text-lg font-bold mt-1">{departmentName}</p>
          <p className="text-sm text-gray-500 mt-2">{description}</p>
          <div className="flex flex-row items-center gap-2 mt-2">
            <Users className="text-gray-500" size={16} />
            <p className="text-sm text-gray-500">
              <span>{employeeCount}</span> employees
            </p>
          </div>
          <div className="flex flex-row items-center gap-2 mt-2">
            <p className="text-sm text-gray-500">Manager: </p>
            <p className="text-sm font-semibold">{managerName}</p>
          </div>

          <button
            onClick={handleCardClick}
            className="mt-4 w-full bg-blue-500 text-white py-2 px-4 rounded-lg text-sm font-medium hover:bg-blue-600 transition-colors"
          >
            View Details
          </button>
        </div>
      </div>
      {editMode && (
        <EditDepartment
          setShowDialog={setEditMode}
          setRefetch={setRefetch}
          department={department}
        />
      )}
    </>
  );
};

export default DepartmentCard;
