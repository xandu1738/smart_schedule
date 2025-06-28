import React from "react";
import AddButton from "../../components/AddButton";
import ScheduleCard from "../../components/ScheduleCard";
import ScheduleDashCard from "../../components/ScheduleDashCard";
import { Calendar, Clock, Users } from "lucide-react";

const Schedules = () => {
  return (
    <>
      <div className="m-8">
        <section className="flex flex-row items-center justify-between">
          <div className="flex flex-col items-start justify-start left-0 w-full">
            <h1 className="font-bold text-3xl">Schedules</h1>
            <p className="text-sm text-gray-500 pt-2">
              Generate and manage shift schedules
            </p>
          </div>
          <div>
            <AddButton onClick={() => {}} buttonName={"Generate Schedule"} />
          </div>
        </section>
		<section className="mt-8 grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
			<ScheduleDashCard value={5} title={"Active Schedules"} icon={<Calendar />} styles={"bg-green-200 rounded-lg p-3 transition-colors text-green-600"}/>
			<ScheduleDashCard value={5} title={"Active Schedules"} icon={<Clock />} styles={"bg-yellow-200 rounded-lg p-3 transition-colors text-amber-700"}/>
			<ScheduleDashCard value={5} title={"Active Schedules"} icon={<Users />} styles={"bg-blue-200 rounded-lg p-3 transition-colors text-blue-500"}/>
		</section>
      </div>
    </>
  );
};

export default Schedules;
