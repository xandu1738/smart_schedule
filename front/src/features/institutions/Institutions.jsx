import React from "react";
import { X } from "lucide-react";
import AddButton from "../../components/AddButton";
import InstitutionCard from "../../components/InstitutionCard";
import { InputText } from "primereact/inputtext";
import { FloatLabel } from "primereact/floatlabel";

const Institutions = (heading, visible, setVisible, fields, textArea) => {
  const [showDialog, setShowDialog] = React.useState(false);
  const [institutionName, setInstitutionName] = React.useState("");

  return (
    <>
      <div className="m-8">
        <section className="flex flex-row items-center justify-between">
          <div className="flex flex-col items-start justify-start left-0 w-full">
            <h1 className="font-bold text-3xl">Institutions</h1>
            <p className="text-sm text-gray-500 pt-2">
              Manage your Institutions
            </p>
          </div>
          <div>
            <AddButton
              onClick={() => setShowDialog(true)}
              buttonName={"Add Institution"}
            />
          </div>
        </section>
        <section className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <InstitutionCard
            institution={"ServiceCops"}
            onClic={() => {}}
            description={"This is a IT firm that changes the scape in Uganda"}
            yearEstablished={"1990"}
            RegNumber={"TIN:12341234213"}
            institutionType={"Private"}
          />
        </section>
      </div>
      {showDialog && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="absolute inset-0 bg-black opacity-50"></div>
          <div className="relative bg-white rounded-lg p-6 shadow-lg w-full max-w-md">
            <div className="flex flex-row items-center justify-between mb-4">
              <h2 className="text-xl font-bold">Add Institution</h2>
              <button
                onClick={() => setShowDialog(false)}
                className="rounded-full p-1 hover:bg-gray-200 hover:text-blue-500 transition-colors"
              >
                <X size={20} />
              </button>
            </div>
            <div>
              <FloatLabel className="card flex justify-content-center">
                <InputText
                  id="institutionName"
                  value={institutionName}
                  onChange={(e) => setInstitutionName(e.target.value)}
                />
                <label htmlFor="institutionName">Institution Name</label>
              </FloatLabel>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Institutions;
