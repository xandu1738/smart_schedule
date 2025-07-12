import { useFormik } from "formik";
import FloatLabelInput from "../../components/FloatLabelInput";
import FloatLabelTextarea from "../../components/FloatLabelTextarea";
import Button from "../../components/Button";
import React from "react";
import { X } from "lucide-react";

const AddDepartment = ({ setShowDialog, setRefetch }) => {
  const formik = useFormik({
    initialValues: {
        name: "",
        description: "",
        managerName: "",
        employeeCount: ""
    },
    onSubmit: (values) => {
      // Handle form submission
      console.log(values);
      setShowDialog(false);
      setRefetch((prev) => prev + 1);
    },
  });

return (
    <>
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            <div className="absolute inset-0 bg-black opacity-50"></div>
            <div className="relative bg-white rounded-lg p-6 shadow-lg w-full max-w-lg m-4 sm:w-[80%] md:w-[60%] lg:w-[40%]">
                <div className="flex flex-row items-center justify-between mb-4">
                    <h2 className="text-xl font-bold">Add Department</h2>
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
                            label="Department Name"
                            name="name"
                            type="text"
                            value={formik.values.name || ""}
                            onChange={formik.handleChange}
                        />
                        <FloatLabelTextarea
                            label="Description"
                            name="description"
                            type="text"
                            value={formik.values.description || ""}
                            onChange={formik.handleChange}
                        />
                        <FloatLabelInput
                            label="Manager Name"
                            name="managerName"
                            type="text"
                            value={formik.values.managerName || ""}
                            onChange={formik.handleChange}
                        />
                        <FloatLabelInput
                            label="Employee Count"
                            name="employeeCount"
                            type="number"
                            value={formik.values.employeeCount || ""}
                            onChange={formik.handleChange}
                        />
                        <Button type={"submit"} onClick={() => {}} className={"w-full mt-4"} buttonName={"Save"} />
                    </div>
                </form>
            </div>
        </div>
    </>
);
};

export default AddDepartment;
 