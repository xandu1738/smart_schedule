import { useNavigate, useParams } from "react-router"
import Button from "../../components/Button"
import LucideIcon from "../../components/LucideIcon"
import { useEffect, useState } from "react"
import { Badge } from "primereact/badge"
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { Avatar } from "primereact/avatar"
import { formatDate, getInitials } from "../../helpers/utils"
import { useGetSingleScheduleQuery } from "../../helpers/redux/slices/extendedApis/scheduleApi"
import Spinner from "../../components/Spinner"
import { useGetEmployeesByDepartmentQuery } from "../../helpers/redux/slices/extendedApis/employeesApi"
import { useAssignToShiftMutation, useGetAllShiftsQuery } from "../../helpers/redux/slices/extendedApis/shiftApi"
import { Tag } from "primereact/tag"

const AssignSchedule = () => {
    const navigate = useNavigate()
    const [search, setSearch] = useState("")
    const [shiftData, setShiftData] = useState({})

    const params = useParams()

    const { data: scheduleInfo, isLoading: scheduleLoading } = useGetSingleScheduleQuery({
        scheduleId: params?.id
    })

    const { data: accounts, isLoading: employeesLoading } = useGetEmployeesByDepartmentQuery(scheduleInfo?.data?.departmentId, {
        skip: !scheduleInfo || !scheduleInfo.data
    })


    const { data: shifts, isLoading: shiftsLoading, error: shiftsError } = useGetAllShiftsQuery({ department_id: scheduleInfo?.data?.departmentId }, {
        skip: !scheduleInfo || !scheduleInfo.data
    });

    const [assignToShift, { isLoading: assignToShiftLoading }] = useAssignToShiftMutation()

    useEffect(() => {
        if (shifts?.data && accounts?.data) {
            const initialData = {
                unassigned: [...accounts.data],
            }

            for (const shift of shifts.data) {
                initialData[shift.id] = []
            }

            setShiftData(initialData)
        }
    }, [shifts?.data, accounts?.data])

    const handleDragEnd = (result) => {
        const { source, destination, draggableId } = result
        if (!destination) return

        if (source.droppableId === destination.droppableId) return

        const sourceList = Array.from(shiftData[source.droppableId] || [])
        const destList = Array.from(shiftData[destination.droppableId] || [])

        // check if destination is not max
        console.log(destList.length)
        console.log(shifts.data.find(shift => shift.id == destination.droppableId)?.maxPeople)
        if (destList.length >= shifts.data.find(shift => shift.id == destination.droppableId)?.maxPeople) {
            alert("Shift has reached its maximum capacity")
            return
        }

        const [moved] = sourceList.splice(source.index, 1)
        destList.splice(destination.index, 0, moved)

        setShiftData(prev => ({
            ...prev,
            [source.droppableId]: sourceList,
            [destination.droppableId]: destList
        }))
    }

    const buildPayload = (shiftData) => {
        const shifts = Object.entries(shiftData)
            .filter(([key]) => key !== 'unassigned')
            .map(([shiftId, employees]) => ({
                shift_id: parseInt(shiftId),
                employees: employees.map(emp => emp.id)
            }));

        return shifts
    };

    const handleGenerateSchedule = () => {
        const payload = buildPayload(shiftData)
        assignToShift({
            "schedule_id": params?.id,
            "shifts": payload
        }).then(() => {
            navigate("/dashboard/schedules")
        }).catch((err) => {
            console.log(err)
        })
    }

    if (scheduleLoading || employeesLoading || assignToShiftLoading) {
        return (<div className="flex items-center justify-center h-screen">
            <Spinner />
        </div>)
    }

    return (
        <div className="flex flex-col gap-4 h-[96vh] overflow-y-hidden">
            <section className="m-2 flex flex-row items-center justify-between">
                <button
                    onClick={() => navigate(-1)}
                    className="flex gap-2 border border-gray-200 rounded-sm bg-white p-2 px-4 hover:bg-gray-100 transition-colors cursor-pointer"
                >
                    <LucideIcon name="ChevronLeft" />
                    Back
                </button>
                <div className="flex flex-1 flex-col gap-0 items-start justify-start left-0 w-full">
                    <h1 className="font-bold text-3xl text-center w-full">Assign employees - {scheduleInfo?.data?.departmentName}</h1>
                    <p className="text-sm text-gray-500 pt-2 text-center w-full">
                        {formatDate(scheduleInfo?.data?.startTime)} - {formatDate(scheduleInfo?.data?.endTime)}
                    </p>
                </div>
                <Button icon={"CheckCheck"} className={"rounded-sm"} buttonName={"Generate schedule"} onClick={handleGenerateSchedule} />
            </section>
            <section className="m-2">
                <div className="flex gap-4 items-center justify-center border-b border-gray-200 rounded-sm p-0 px-4">
                    <LucideIcon
                        name={search ? "X" : "Search"}
                        onClick={() => setSearch("")}
                        className="cursor-pointer text-gray-400 text-sm"
                    />
                    <input
                        type="text"
                        placeholder="Search ..."
                        className="w-full py-0 px-0 border-none border-gray-200 rounded-sm"
                    />
                </div>
            </section>

            <DragDropContext onDragEnd={handleDragEnd}>
                <div className="overflow-y-hidden m-2 mb-4 flex-1 grid grid-cols-12 gap-2">

                    {/* Unassigned Employees */}
                    <div className="overflow-y-hidden col-span-4 border border-gray-200 rounded-sm p-4 bg-white">
                        <div className="flex gap-2 items-center justify-center p-2">
                            <LucideIcon name="Users" />
                            <h2 className="font-bold text-2xl text-center w-full text-gray-500">
                                Employees
                            </h2>
                            <Badge value={shiftData?.unassigned?.length} severity="success"></Badge>
                        </div>
                        <Droppable droppableId="unassigned">
                            {(provided, snapshot) => (
                                <div
                                    ref={provided.innerRef}
                                    {...provided.droppableProps}
                                    className={`h-[95%] overflow-y-auto flex flex-col gap-2 space-y-2 p-2 rounded-lg transition-colors ${snapshot.isDraggingOver ? 'bg-blue-50' : 'bg-gray-50'
                                        }`}
                                >
                                    {shiftData?.unassigned?.map((account, index) => (
                                        <Draggable key={account.id} draggableId={`${account.id}`} index={index}>
                                            {(provided, snapshot) => (
                                                <div
                                                    ref={provided.innerRef}
                                                    {...provided.draggableProps}
                                                    {...provided.dragHandleProps}
                                                    className={`p-3 bg-white rounded-lg border shadow-sm cursor-move transition-shadow hover:shadow-md ${snapshot.isDragging ? 'shadow-lg' : ''
                                                        }`}
                                                >
                                                    <div className="flex items-center space-x-3">
                                                        <Avatar name={account.name} className="w-8 h-8">
                                                            {getInitials(account.name)}
                                                        </Avatar>
                                                        <div className="flex-1 min-w-0">
                                                            <p className="font-medium text-sm text-gray-900 truncate">
                                                                {account.name}
                                                            </p>
                                                            <p className="text-xs text-gray-500 truncate">
                                                                {account.email}
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            )}
                                        </Draggable>
                                    ))}
                                    {provided.placeholder}
                                </div>
                            )}
                        </Droppable>
                    </div>

                    {/* className="grid auto-flow-col gap-2 w-max"
    style={{ gridAutoColumns: 'minmax(200px, 1fr)' }} */}

                    {/* Shift Columns */}
                    {/* <div className="col-span-9 grid grid-cols-12 gap-2 overflow-x-auto"> */}
                    <div className="col-span-8 grid grid-flow-col auto-cols-[minmax(400px,_1fr)] gap-2 overflow-x-auto">
                        {
                            shifts?.data?.map((shift) => (<>
                                <div className="rounded-sm p-4 bg-white">
                                    <div className="flex gap-2 items-center justify-center p-2">
                                        <LucideIcon name="Users" />
                                        <h2 className="font-bold text-2xl text-center flex-1 text-gray-500">
                                            {shift.name}
                                        </h2>
                                        <Tag value={`${shiftData[shift.id]?.length} / ${shift?.maxPeople}`} severity="success"></Tag>
                                    </div>

                                    <Droppable droppableId={`${shift.id}`}>
                                        {(provided, snapshot) => (
                                            <div
                                                ref={provided.innerRef}
                                                {...provided.droppableProps}
                                                className={`min-h-[400px] flex flex-col gap-2 space-y-2 p-2 rounded-lg transition-colors ${snapshot.isDraggingOver ? 'bg-blue-50' : 'bg-gray-50'
                                                    }`}
                                            >
                                                {shiftData?.[shift?.id]?.map((account, index) => (
                                                    <Draggable key={account?.id} draggableId={`${account?.id}`} index={index}>
                                                        {(provided, snapshot) => (
                                                            <div
                                                                ref={provided.innerRef}
                                                                {...provided.draggableProps}
                                                                {...provided.dragHandleProps}
                                                                className={`p-3 bg-white rounded-lg border shadow-sm cursor-move transition-shadow hover:shadow-md ${snapshot.isDragging ? 'shadow-lg' : ''
                                                                    }`}
                                                            >
                                                                <div className="flex items-center space-x-3">
                                                                    <Avatar name={account?.name} className="w-8 h-8">
                                                                        {getInitials(account?.name)}
                                                                    </Avatar>
                                                                    <div className="flex-1 min-w-0">
                                                                        <p className="font-medium text-sm text-gray-900 truncate">
                                                                            {account?.name}
                                                                        </p>
                                                                        <p className="text-xs text-gray-500 truncate">
                                                                            {account?.email}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        )}
                                                    </Draggable>
                                                ))}
                                                {provided.placeholder}

                                                {/* {accounts?.data?.map((account, index) => (
                                                    <Draggable key={account.id} draggableId={`${account.id}`} index={index}>
                                                        {(provided, snapshot) => (
                                                            <div
                                                                ref={provided.innerRef}
                                                                {...provided.draggableProps}
                                                                {...provided.dragHandleProps}
                                                                className={`p-3 bg-white rounded-lg border shadow-sm cursor-move transition-shadow hover:shadow-md ${snapshot.isDragging ? 'shadow-lg' : ''
                                                                    }`}
                                                            >
                                                                <div className="flex items-center space-x-3">
                                                                    <Avatar name={account.name} className="w-8 h-8">
                                                                        {getInitials(account.name)}
                                                                    </Avatar>
                                                                    <div className="flex-1 min-w-0">
                                                                        <p className="font-medium text-sm text-gray-900 truncate">
                                                                            {account.name}
                                                                        </p>
                                                                        <p className="text-xs text-gray-500 truncate">
                                                                            {account.email}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        )}
                                                    </Draggable>
                                                ))} */}
                                            </div>
                                        )}
                                    </Droppable>
                                </div>

                            </>))
                        }
                    </div>
                </div>
            </DragDropContext>
        </div>
    )
}

export default AssignSchedule