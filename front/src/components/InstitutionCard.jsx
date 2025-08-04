// Ownership (e.g., Public, Private, NGO, Mission-based)

// Year Established

// Registration/Accreditation Number
import { Users, Pencil, Landmark } from "lucide-react";

const InstitutionCard = ({
  institution,
  onClick,
  description,
  yearEstablished,
  RegNumber,
  institutionType,
}) => {
  return (
    <>
      <div className="shadow-md hover:shadow-lg rounded-lg p-4 m-4 bg-white w-auto">
        <div className="flex flex-row items-center justify-between mb-4">
          <div className="bg-blue-600 rounded-lg p-3 transition-colors">
            <Landmark className="text-white" />
          </div>
          <button onClick={onClick} className="text-sm">
            <Pencil size={20} />
          </button>
        </div>
        <div className="flex flex-col items-start">
          <p className="text-lg font-bold mt-1">{institution}</p>
          <p className="text-sm text-gray-500 mt-2">{description}</p>
          <p className="text-sm text-gray-500"><span>Institution Type: </span>{institutionType}</p>
          <div className="flex flex-row items-center gap-2 mt-2">
            {/* <Users className="text-gray-500" size={16} /> */}
            <p className="text-sm text-gray-500">
              Year Established: <span>{yearEstablished}</span>
            </p>
          </div>
          <div className="flex flex-row items-center gap-2 mt-2">
            <p className="text-sm text-gray-500">Reg Number: </p>
            <p className="text-sm font-semibold">{RegNumber}</p>
          </div>
        </div>
      </div>
    </>
  );
};

export default InstitutionCard;
