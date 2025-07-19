import { Users, Pencil, Building2 } from "lucide-react";
import React from "react";
import { useNavigate } from "react-router";
import { APP_ROUTE } from "../config/route.config.js";

const DepartmentCard = ({
  department,
  employeeCount,
  description,
  managerName,
  onEdit,
  departmentId
}) => {
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
          <button onClick={() => onEdit()} className="text-sm">
            <Pencil size={20} />
          </button>
        </div>
        <div className="flex flex-col items-start">
          <p className="text-lg font-bold mt-1">{department}</p>
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
        </div>
      </div>
    </>
  );
};

export default DepartmentCard;
