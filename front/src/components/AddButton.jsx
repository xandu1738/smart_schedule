import { Plus } from 'lucide-react';

const AddButton = ({ onClick, buttonName }) => {
  return (
    <button
      className="bg-blue-500 text-white px-4 py-2 text-sm font-semibold rounded-lg hover:bg-blue-600 transition-colors flex items-center gap-2 whitespace-nowrap"
      onClick={onClick}
      style={{ whiteSpace: 'nowrap' }}
    >
      <span><Plus /></span> {buttonName}
    </button>
  );
};

export default AddButton;
