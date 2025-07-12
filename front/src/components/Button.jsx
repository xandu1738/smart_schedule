const Button = ({ onClick, buttonName, className, type }) => {
return (
    <button
        className={`${className} bg-blue-500 text-white px-4 py-2 text-sm font-semibold rounded-lg hover:bg-blue-600 transition-colors flex justify-center items-center gap-2 whitespace-nowrap`}
        type={type}
        onClick={onClick}
        style={{ whiteSpace: 'nowrap' }}
    >
        {buttonName}
    </button>
);
};

export default Button;
