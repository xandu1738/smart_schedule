const ScheduleDashCard = ({ value, title, icon }) => {
    return (
        <>
            <div className="flex flex-col rounded-lg shadow-md hover:shadow-lg p-4 m-4 w-auto">
                
                <div className="flex flex-row items-center justify-between mb-4">
                    <div className="flex flex-col items-start">
                        <p className="flex items-start text-sm font-semibold text-gray-600" >{title}</p>
                        <p className="text-3xl font-bold mt-3">{value}</p>
                    </div>
                    <div className="bg-blue-600 rounded-lg p-3 transition-colors text-white">
                        {icon}
                    </div>
                </div>
            </div>
        </>
    )
}

export default ScheduleDashCard;