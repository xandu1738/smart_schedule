import { useFormik } from "formik";
import React from "react";
import { X } from "lucide-react";
import Button from "../../components/Button";
import Spinner from "../../components/Spinner";
import FloatLabelTextarea from "../../components/FloatLabelTextarea";
import FloatLabelInput from "../../components/FloatLabelInput";

const EditInstitutionDialog = ({ setShowDialog, setRefetch }) => {
  // const [showDialog, onClose] = React.useState(false);

  const formik = useFormik({
    initialValues: {
      name: "",
      description: "",
      ownerName: "",
      location: "",
      regNo: "",
      yearEstablished: "",
    },
    onSubmit: (values) => {
      // Handle form submission
      console.log(values);
      onClose(false);
      setRefetch((prev) => prev + 1);
    },
  });

  return (
    <>
      <div className="fixed inset-0 z-50 flex items-center justify-center ">
        <div className="absolute inset-0 bg-black opacity-50"></div>
        <div className="relative bg-white rounded-lg p-6 shadow-lg w-[40%] m-4">
          <div className="flex flex-row items-center justify-between mb-4">
            <h2 className="text-xl font-bold">Edit Institution</h2>
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
                label="Institution Name"
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
                label="Owner Name"
                name="ownerName"
                type="text"
                value={formik.values.ownerName || ""}
                onChange={formik.handleChange}
              />
              <FloatLabelInput
                label="Institution Name"
                name="name"
                type="text"
                value={formik.values.name || ""}
                onChange={formik.handleChange}
              />
              <FloatLabelInput
                label="Institution Location"
                name="location"
                type="text"
                value={formik.values.location || ""}
                onChange={formik.handleChange}
              />
              <FloatLabelInput
                label="Registration Number"
                name="regNo"
                type="text"
                value={formik.values.regNo || ""}
                onChange={formik.handleChange}
              />
              <FloatLabelInput
                label="Year Established"
                name="yearEstablished"
                type="text"
                value={formik.values.yearEstablished || ""}
                onChange={formik.handleChange}
              />
            </div>
            <Button
              type={"submit"}
              onClick={() => {}}
              className={"w-full mt-4"}
              buttonName={"Edit"}
            />
          </form>
        </div>
      </div>
    </>
  );
};

export default EditInstitutionDialog;
