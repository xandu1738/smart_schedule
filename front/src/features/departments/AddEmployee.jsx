import { useFormik } from "formik";
import FloatLabelInput from "../../components/FloatLabelInput";
import FloatLabelSelect from "../../components/FloatLabelSelect";
import Button from "../../components/Button";
import Spinner from "../../components/Spinner";
import React from "react";
import { X } from "lucide-react";
import { useAddEmployeeMutation } from "../../helpers/redux/slices/extendedApis/employeesApi";

const AddEmployee = ({
  setShowDialog,
  departmentId,
  departmentName,
  setRefetch,
}) => {
  const [saveEmployee, { isLoading }] = useAddEmployeeMutation();

  const formik = useFormik({
    initialValues: {
      name: "",
      department: parseInt(departmentId),
      email: "",
      status: "Active",
      days_off_used: 0,
      archived: true,
    },
    onSubmit: (values) => {
      saveEmployee(values)
        .then((res) => {
          console.log("Employee added successfully:", res);
        })
        .catch((err) => {
          console.error("Error adding employee:", err);
        })
        .finally(() => {
          setShowDialog(false);
          if (setRefetch) {
            setRefetch((prev) => prev + 1);
          }
        });
    },
  });

  const statusOptions = [
    { value: "Active", label: "Active" },
    { value: "On Leave", label: "On Leave" },
    { value: "Inactive", label: "Inactive" },
  ];

  return (
    <>
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black opacity-50"></div>
        <div className="relative bg-white rounded-lg p-6 shadow-lg w-full max-w-lg m-4 sm:w-[80%] md:w-[60%] lg:w-[40%]">
          <div className="flex flex-row items-center justify-between mb-4">
            <h2 className="text-xl font-bold">Add Employee</h2>
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
                label="Full Name"
                name="name"
                type="text"
                value={formik.values.name || ""}
                onChange={formik.handleChange}
              />

              <FloatLabelInput
                label="Email Address"
                name="email"
                type="email"
                value={formik.values.email || ""}
                onChange={formik.handleChange}
              />

              <FloatLabelSelect
                label="Status"
                name="status"
                value={formik.values.status || ""}
                onChange={formik.handleChange}
                options={statusOptions}
                placeholder="Select status"
              />

              <FloatLabelInput
                label="Days Off Used"
                name="days_off_used"
                type="number"
                value={formik.values.days_off_used || ""}
                onChange={formik.handleChange}
              />

              <div className="text-sm text-gray-500 mb-4">
                Department: {departmentName}
              </div>

              <div className="text-sm text-gray-500 mb-4">
                Note: Archived status is set to active by default
              </div>

              <Button
                type={"submit"}
                onClick={() => {}}
                className={"w-full mt-4"}
                buttonName={isLoading ? <Spinner /> : "Add Employee"}
                disabled={isLoading}
              />
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default AddEmployee;
