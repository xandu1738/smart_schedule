import { Users, Pencil, Building2 } from "lucide-react";

const DepartmentCard = ({
  department,
  onClick,
  employeeCount,
  description,
  managerName
}) => {
  return (
    <>
      <div className="shadow-md hover:shadow-lg rounded-lg p-4 m-4 w-auto">
        <div className="flex flex-row items-center justify-between mb-4">
            <div className="bg-blue-600 rounded-lg p-3 transition-colors"><Building2 className="text-white"/></div>
            <button onClick={onClick} className="text-sm"><Pencil size={20}/></button>
        </div>
        <div className="flex flex-col items-start">
            <p className="text-lg font-bold mt-1">{department}</p>
            <p className="text-sm text-gray-500 mt-2">{description}</p>
            <div className="flex flex-row items-center gap-2 mt-2">
                <Users className="text-gray-500" size={16} />
                <p className="text-sm text-gray-500"><span>{employeeCount}</span> employees</p>
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
