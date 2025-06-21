import React from "react";
import { Mail, Phone, User } from "lucide-react";

const EmployeeCard = ({ email, number, name, department, status, role }) => {

  const getInitials = (name) => {
    if (!name) return "";
    const parts = name.trim().split(" ");
    if (parts.length === 1) {
      return parts[0][0]?.toUpperCase() || "";
    }
    return (parts[0][0] + parts[1][0]).toUpperCase();
  };

return (
    <>
        <div className="shadow-md hover:shadow-lg rounded-lg p-4 m-4 w-auto">
            <div className="flex flex-row items-center justify-between mb-4">
                <div className="flex flex-row items-start gap-2">
                    <div className="bg-blue-600 rounded-full h-14 w-14 flex items-center justify-center mb-2">
                        <span className="text-white font-bold text-2xl">
                            {getInitials(name)}
                        </span>
                    </div>
                    <div className="flex flex-col items-start">
                        <h2 className="text-lg font-bold">{name}</h2>
                        <p className="text-sm text-gray-500">{role}</p>
                    </div>
                </div>
                <div className="rounded-2xl bg-black py-1 px-3 flex items-center justify-center h-5">
                    <p className="text-white text-sm">{status}</p>
                </div>
            </div>
            <div className="flex flex-col items-start flex-1">
                <div className="flex flex-row items-center gap-1 text-gray-500 mb-2">
                    <Mail className="text-sm" size={16} />
                    <p className="text-sm">{email}</p>
                </div>
                <div className="flex flex-row items-center gap-1 text-gray-500 mb-2">
                    <Phone className="text-sm" size={16} />
                    <p className="text-sm">{number}</p>
                </div>
                <div className="flex flex-row items-center gap-1 text-gray-500">
                    <User className="text-sm" size={16} />
                    <p className="text-sm">{department}</p>
                </div>
            </div>
        </div>
    </>
);
};

export default EmployeeCard;
