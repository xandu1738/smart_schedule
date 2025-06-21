import { ArrowUpRight } from 'lucide-react'

const DashboardCard = ({ title, value, icon, stat }) => {
    return (
        <>
            <div className="shadow-md hover:shadow-lg rounded-lg p-4 m-4 w-auto">
                <p className="flex items-start text-sm font-medium text-gray-600">{title}</p>
                <div className="flex flex-row items-center justify-between mb-4">
                    <p className="text-3xl font-bold">{value}</p>
                    <div className="bg-blue-600 rounded-lg p-3 transition-colors text-white">
                        {icon}
                    </div>
                </div>
                <div className="flex flex-row items-center mt-2 gap-1">
                    <p>↗️ <span className='text-sm text-green-600'>{stat} this month</span></p>
                </div>
            </div>
        </>
    )
}

export default DashboardCard;