import { useFormik } from "formik";
import FloatLabelInput from "../../components/FloatLabelInput";
import FloatLabelSelect from "../../components/FloatLabelSelect";
import Button from "../../components/Button";
import React from "react";
import { X } from "lucide-react";

const AddShift = ({ setShowDialog, departmentId, setRefetch }) => {
  // Mock save function - replace with actual API mutation when available
  const saveShift = async (data) => {
    console.log("Saving shift:", data);
    // Simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ success: true });
      }, 1000);
    });
  };

  const formik = useFormik({
    initialValues: {
      department_id: departmentId,
      shift_type: "",
      name: "",
      end_time: "",
      start_time: "",
      max_people: "",
    },
    onSubmit: (values) => {
      saveShift(values)
        .then((res) => {
          console.log("Shift added successfully:", res);
        })
        .catch((err) => {
          console.error("Error adding shift:", err);
        })
        .finally(() => {
          setShowDialog(false);
          if (setRefetch) {
            setRefetch((prev) => prev + 1);
          }
        });
    },
  });

  const shiftTypeOptions = [
    { value: "DAILY_SHIFT", label: "Daily Shift" },
    { value: "WEEKLY_SHIFT", label: "Weekly Shift" },
    { value: "MONTHLY_SHIFT", label: "Monthly Shift" },
  ];

  return (
    <>
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black opacity-50"></div>
        <div className="relative bg-white rounded-lg p-6 shadow-lg w-full max-w-lg m-4 sm:w-[80%] md:w-[60%] lg:w-[40%]">
          <div className="flex flex-row items-center justify-between mb-4">
            <h2 className="text-xl font-bold">Create New Shift</h2>
            <button
              onClick={() => setShowDialog(false)}
              className="rounded-full p-1 hover:bg-gray-200 hover:text-blue-500 transition-colors"
            >
              <X size={20} />
            </button>
          </div>
          <form onSubmit={formik.handleSubmit} className="space-y-4">
            <div className="flex flex-col justify-center items-center">
              <FloatLabelInput
                label="Shift Name"
                name="name"
                type="text"
                value={formik.values.name || ""}
                onChange={formik.handleChange}
              />

              <FloatLabelSelect
                label="Shift Type"
                name="shift_type"
                value={formik.values.shift_type || ""}
                onChange={formik.handleChange}
                options={shiftTypeOptions}
                placeholder="Select shift type"
              />

              <FloatLabelInput
                label="Start Time"
                name="start_time"
                type="date"
                value={formik.values.start_time || ""}
                onChange={formik.handleChange}
              />

              <FloatLabelInput
                label="End Time"
                name="end_time"
                type="date"
                value={formik.values.end_time || ""}
                onChange={formik.handleChange}
              />

              <FloatLabelInput
                label="Maximum People"
                name="max_people"
                type="number"
                value={formik.values.max_people || ""}
                onChange={formik.handleChange}
              />

              <Button
                type={"submit"}
                onClick={() => {}}
                className={"w-full mt-4"}
                buttonName={"Create Shift"}
              />
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default AddShift;
