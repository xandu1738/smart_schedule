import {Clock, Clock4, Users, Calendar } from "lucide-react";

const ShiftCard = ({ employeeCount, department, shift, status }) => {
return (
    <div className="shadow-md hover:shadow-lg rounded-lg p-6 m-4 w-auto">
        <div className="flex flex-row items-center justify-between mb-4">
            <div className="bg-blue-600 rounded-lg p-3 transition-colors">
                <Clock className="text-white" size={24} />
            </div>
            <div className="rounded-2xl bg-black py-1 px-3 flex items-center justify-center h-5">
                <p className="text-white text-sm">{status}</p>
            </div>
        </div>
        <div className="flex flex-col items-start">
            <p className="font-semibold text-xl">{shift} Shift</p>
            <div className="flex flex-row items-center gap-2 mt-2">
                <Clock4 className="text-gray-500" size={16} />
                <p className="text-sm text-gray-500">09:00 - 17:00</p>
            </div>
            <div className="flex flex-row items-center gap-2 mt-2">
                <Users className="text-gray-500" size={16} />
                <p className="text-sm text-gray-500"><span>{employeeCount}</span> employees</p>
            </div>
            <div className="flex flex-row items-center gap-2 mt-2">
                <Calendar className="text-gray-500" size={16} />
                <p className="text-sm text-gray-500">Department: {department}</p>
            </div>
            <p className="text-xs text-gray-500 mt-2">Work Days:</p>
            <div className="flex items-start justify-center h-5 mt-2">
                <p className="border border-gray-300 p-1 font-semibold text-xs rounded-2xl px-2">Mon</p>
            </div>
        </div>
    </div>
);
};

export default ShiftCard;
