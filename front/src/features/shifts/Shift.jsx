import React from "react";
import AddButton from "../../components/AddButton";
import ShiftCard from "../../components/ShiftCard";

const Shift = () => {
  return (
    <>
      {/* <p>shift</p> */}
      <div className="m-8">
        <section className="flex flex-row items-center justify-between">
          <div className="flex flex-col items-start justify-start left-0 w-full">
            <h1 className="font-bold text-3xl">Shifts</h1>
            <p className="text-sm text-gray-500 pt-2">
              Define and manage shift patterns
            </p>
          </div>
          <div>
            <AddButton onClick={() => {}} buttonName={"Create Shift"} />
          </div>
        </section>
        <section className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <ShiftCard
            employeeCount={10}
            department={"IT Department"}
            shift={"Evening Shift"}
            status={"Active"}
          />
          <ShiftCard
            employeeCount={10}
            department={"IT Department"}
            shift={"Evening Shift"}
            status={"Active"}
          />
          <ShiftCard
            employeeCount={10}
            department={"IT Department"}
            shift={"Evening Shift"}
            status={"Active"}
          />
          <ShiftCard
            employeeCount={10}
            department={"IT Department"}
            shift={"Evening Shift"}
            status={"Active"}
          />
        </section>
      </div>
    </>
  );
};

export default Shift;
