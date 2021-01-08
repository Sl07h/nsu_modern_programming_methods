#include <cassert>
#include <functional>
#include <memory>

// DILifetime на 11 стандарте, используя кучу
// зависимость передаётся через конструктор, используя move семантику
template <typename I, typename S>
class DILifetime {
public:
    template <typename... Args>
    DILifetime(I*& member, Args&&... args)
        : item_(new S(std::forward<Args>(args)...)),
        member_(&member)
    {
        *member_ = item_.get();
    }

    DILifetime(const DILifetime& other) = delete;
    DILifetime& operator=(const DILifetime& other) = delete;

    DILifetime(DILifetime&& other)
        : item_(std::move(other.item_)),
        member_(other.member_)
    {
        other.member_ = nullptr;
    }

    DILifetime& operator=(DILifetime&& other)
    {
        item_ = std::move(other.item_);
        member_ = other.member_;
        other.member_ = nullptr;
        return *this;
    }

    ~DILifetime() {
        if (member_)
            *member_ = nullptr;
    }

    const S& getComponent() const { return *item_; }
    S& getComponent() { return *item_; }

private:
    std::unique_ptr<S> item_;
    I** member_ = nullptr;
};



// сам контейнер
class DI {

private:
    class DoThingPipeline* doThingPipeline_ = nullptr;

public:
    DoThingPipeline& getDoThingPipeline() const {
        assert(doThingPipeline_);
        return *doThingPipeline_;
    }

    template <typename S = DoThingPipeline>
    DILifetime<DoThingPipeline, S> installDoThingPipeline() {
        assert(!doThingPipeline_);
        return DILifetime<DoThingPipeline, S>(doThingPipeline_);
    }

private:
    class PowerFinder* powerFinder_ = nullptr;
    class VirtualPowerFinder* virtualPowerFinder_ = nullptr;
    class StaticPowerFinder* staticPowerFinder_ = nullptr;

public:
    const PowerFinder& getPowerFinder() const {
        assert(powerFinder_);
        return *powerFinder_;
    }

    VirtualPowerFinder& getVirtualPowerFinder() const {
        assert(virtualPowerFinder_);
        return *virtualPowerFinder_;
    }

    StaticPowerFinder& getStaticPowerFinder() const {
        assert(staticPowerFinder_);
        return *staticPowerFinder_;
    }

    template <typename S = PowerFinder>
    DILifetime<PowerFinder, S> installPowerFinder() {
        assert(!powerFinder_);
        return DILifetime<PowerFinder, S>(powerFinder_);
    }

    template <typename S>
    DILifetime<VirtualPowerFinder, S> installVirtualPowerFinder() {
        assert(!virtualPowerFinder_);
        return DILifetime<VirtualPowerFinder, S>(virtualPowerFinder_);
    }

    template <typename S = StaticPowerFinder, typename... Args>
    DILifetime<StaticPowerFinder, S> installStaticPowerFinder(Args&&... args) {
        assert(!staticPowerFinder_);
        return DILifetime<StaticPowerFinder, S>(staticPowerFinder_, std::forward<Args>(args)...);
    }
};

