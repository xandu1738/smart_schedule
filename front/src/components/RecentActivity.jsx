import { Dot } from 'lucide-react';

const RecentActivity = () => {
return (
    <div className="flex flex-col rounded-lg shadow-md hover:shadow-lg p-4 m-4 w-auto">
        <div className="flex items-start">
            <p className="text-lg font-semibold">Recent Activity</p>
        </div>

        <div className="flex flex-col items-start mt-4">
            <div className="rounded-lg bg-green-100 p-3 mb-2 w-full flex flex-row items-center gap-1">
                <Dot className="text-green-600" size={50} />
                <div className="flex flex-col items-start">
                    <p className="text-sm font-semibold">John Doe completed the onboarding process.</p>
                    <p className="text-xs text-gray-500">2 hours ago</p>
                </div>
            </div>
        </div>
    </div>
);
};

export default RecentActivity;
